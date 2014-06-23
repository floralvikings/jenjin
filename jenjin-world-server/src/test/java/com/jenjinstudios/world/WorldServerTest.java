package com.jenjinstudios.world;

import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.util.Files;
import com.jenjinstudios.world.io.WorldFileReader;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.sql.WorldSQLHandler;
import com.jenjinstudios.world.state.MoveState;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Test the world server.
 * @author Caleb Brinkman
 */
public class WorldServerTest
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldServerTest.class.getName());
	/** The current test account being used. */
	public static int testAccountNumber = 0;
	/** The port used to listen and connect. */
	public static int port = WorldServer.DEFAULT_PORT;
	/** The String used in connection protocol. */
	public static final String CONNECTION_STRING_PROTOCOL = "jdbc:mysql:thin://";
	private static final String dbAddress = "localhost";
	private static final String dbName = "jenjin_test";
	private static final String dbUsername = "jenjin_user";
	private static final String dbPassword = "jenjin_password";
	private static final String dbUrl = CONNECTION_STRING_PROTOCOL + dbAddress + "/" + dbName;

	/**
	 * The tolerance for distance between a the client and server positions of an actor. This is roughly how much an
	 * actor should move during an update (assuming default UPS, which these tests do).  This means that the client and
	 * server can have about one update worth of discrepancy between them before the tests fail.  This is intended to
	 * avoid spurious test failures that could be caused by unforeseen lag on one of the threads.
	 */
	public static final double vectorTolerance = (Actor.MOVE_SPEED / (double) WorldServer.DEFAULT_UPS) * 1.1;
	private static final MessageRegistry mr = new MessageRegistry();

	/**
	 * Construct the test.
	 * @throws Exception If there's an Exception.
	 */
	@BeforeClass
	public static void construct() throws Exception {
		InputStream configFile = WorldServerTest.class.getResourceAsStream("/com/jenjinstudios/logger.properties");
		LogManager.getLogManager().readConfiguration(configFile);
	}

	/**
	 * Set up the client and server.
	 * @throws Exception If there's an exception.
	 */
	@BeforeMethod
	public static void setUp() throws Exception {
		testAccountNumber++;
		port++;
	}

	/**
	 * Assert that the player for the given client is at the given vector.
	 * @param client The client.
	 * @param vector1 The vector.
	 */
	public static void assertClientAtVector(WorldClient client, Vector2D vector1) {
		assertClientAtVector(client, vector1, vectorTolerance);
	}

	/**
	 * Assert that the player for the given client is at the given vector.
	 * @param client The client.
	 * @param vector1 The vector.
	 * @param tolerance The tolerable difference between the client's vector and the expected vector.
	 */
	public static void assertClientAtVector(WorldClient client, Vector2D vector1, double tolerance) {
		double distance = vector1.getDistanceToVector(client.getPlayer().getVector2D());
		Assert.assertEquals(distance, 0, tolerance,
				"V: " + client.getPlayer().getVector2D() + " " + "V1: " + vector1);
	}

	/**
	 * Initialize and log the client in.
	 * @param port The port number on which to start the client.
	 * @return The initialized, logged in client.
	 * @throws Exception If there's an exception.
	 */
	public static WorldClient initWorldClient(int port) throws Exception {
		String user = "TestAccount" + testAccountNumber;
		LOGGER.log(Level.INFO, "Logging into account {0}", user);
		Socket sock = new Socket("localhost", port);
		MessageInputStream in = new MessageInputStream(mr, sock.getInputStream());
		MessageOutputStream out = new MessageOutputStream(mr, sock.getOutputStream());
		WorldClient worldClient = new WorldClient(in, out, mr, user, "testPassword", new File("resources/WorldTestFile.xml"));
		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		return worldClient;
	}

	/**
	 * Initialize the world and world server.
	 * @param port The port on which to initialize the server.
	 * @return The initialized server.
	 * @throws Exception If there's an exception.
	 */
	public static WorldServer initWorldServer(int port) throws Exception {
		/* The world SQL handler used to test. */
		WorldSQLHandler worldSQLHandler = getSqlHandler();
		WorldServer worldServer = new WorldServer(mr,
				WorldServer.DEFAULT_UPS, port, WorldClientHandler.class, worldSQLHandler, new WorldFileReader(
				WorldServerTest.class.getResourceAsStream("/com/jenjinstudios/world/WorldFile01.xml"))
		);
		worldServer.blockingStart();
		return worldServer;
	}

	/**
	 * Move the specified actor to within one STEP_LENGTH of the specified vector.
	 * @param serverActor The actor.
	 * @param newVector The target vector.
	 * @throws InterruptedException If there is an error blocking until the target is reached.
	 */
	public static void moveServerActorToVector(Actor serverActor, Vector2D newVector) throws InterruptedException {
		double newAngle = serverActor.getVector2D().getAngleToVector(newVector);
		serverActor.setRelativeAngle(newAngle);
		double distanceToNewVector = serverActor.getVector2D().getDistanceToVector(newVector);
		while (distanceToNewVector > vectorTolerance && (serverActor.getForcedState() == null))
		{
			Thread.sleep(10);
			distanceToNewVector = serverActor.getVector2D().getDistanceToVector(newVector);
		}
		serverActor.setRelativeAngle(MoveState.IDLE);
		// Give client time to "catch up".
		Thread.sleep(100);
	}

	/**
	 * Move the client and server player to the given vector, by initiating the move client-side.  Also sends a ping to
	 * the server with each sleep cycle.
	 * @param client The client.
	 * @param server The server.
	 * @param target The vector to which to move.
	 * @throws InterruptedException If there's an exception.
	 */
	public static void movePlayerToVector(WorldClient client, WorldServer server, Vector2D target) throws InterruptedException {
		ClientPlayer clientPlayer = client.getPlayer();
		String username = client.getUsername();
		Player serverPlayer = server.getClientHandlerByUsername(username).getPlayer();
		double angle = clientPlayer.getVector2D().getAngleToVector(target);
		double dist = clientPlayer.getVector2D().getDistanceToVector(target);
		if (dist <= vectorTolerance) return;
		clientPlayer.setRelativeAngle(angle);
		long timeToSleep = (long) (1000 * (dist / ClientActor.MOVE_SPEED));
		// Have to wait for the new angle to be set
		for (int i = 0; i < timeToSleep; i += 10)
		{
			Thread.sleep(10);
			if (clientPlayer.getVector2D().getDistanceToVector(target) <= vectorTolerance)
			{
				break;
			}
		}
		clientPlayer.setRelativeAngle(MoveState.IDLE);
		Thread.sleep(100);
		while (serverPlayer.getRelativeAngle() != MoveState.IDLE)
		{
			Thread.sleep(2);
		}
	}

	/**
	 * Assert the client and the server actor are within one vectorTolerance of each other.
	 * @param serverActor The server actor.
	 * @param clientActor The client actor.
	 */
	public static void assertClientAndServerInSamePosition(Actor serverActor, WorldObject clientActor) {
		double distance = serverActor.getVector2D().getDistanceToVector(clientActor.getVector2D());
		Assert.assertEquals(distance, 0, vectorTolerance, "Server Vector: " + serverActor.getVector2D() +
				" Client Vector: " + clientActor.getVector2D());
	}

	/**
	 * Tear down the client and server.
	 * @param client The client.
	 * @param server The server.
	 * @throws Exception If there's an exception.
	 */
	public static void tearDown(WorldClient client, WorldServer server) throws Exception {
		Player serverPlayer = server.getClientHandlerByUsername(client.getUsername()).getPlayer();
		serverPlayer.setVector2D(new Vector2D(0, 0));
		client.sendBlockingLogoutRequest();
		LOGGER.log(Level.INFO, "Shutting down WorldClient. Avg. ping was {0}", client.getAveragePingTime());
		client.shutdown();
		server.shutdown();

		File resourcesDir = new File("resources/");
		Files.deleteRecursively(resourcesDir);
	}

	/**
	 * Reset the database in the even of a test failure.
	 * @throws Exception If there's an exception.
	 */
	@AfterClass
	public static void resetDB() throws Exception {
		WorldSQLHandler worldSQLHandler = getSqlHandler();
		for (int i = 1; i <= testAccountNumber; i++)
		{
			String user = "TestAccount" + i;
			worldSQLHandler.logOutPlayer(new Actor(user));
		}
	}

	public static WorldSQLHandler getSqlHandler() throws SQLException {
		try
		{
			Class.forName("org.drizzle.jdbc.DrizzleDriver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to register Drizzle driver; is the Drizzle dependency present?");
		}
		Connection dbConnection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		return new WorldSQLHandler(dbConnection);
	}
}
