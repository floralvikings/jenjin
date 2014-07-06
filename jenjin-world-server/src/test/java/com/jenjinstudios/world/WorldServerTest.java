package com.jenjinstudios.world;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.util.Files;
import com.jenjinstudios.server.net.ClientListenerInit;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.sql.WorldAuthenticator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.InputStream;
import java.net.BindException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
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
	/** The port used to listen and connect. */
	private static int port = WorldServer.DEFAULT_PORT;
	private static int connectionNumber = 0;

	/**
	 * The tolerance for distance between a the client and server positions of an actor. This is roughly how much an
	 * actor should move during an update (assuming default UPS, which these tests do).  This means that the client and
	 * server can have about one update worth of discrepancy between them before the tests fail.  This is intended to
	 * avoid spurious test failures that could be caused by unforeseen lag on one of the threads.
	 */
	protected static final double vectorTolerance = (Actor.MOVE_SPEED / (double) WorldServer.DEFAULT_UPS) * 1.1;
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
	 * Assert that the player for the given client is at the given vector.
	 * @param client The client.
	 * @param vector1 The vector.
	 */
	protected static void assertClientAtVector(WorldClient client, Vector2D vector1) {
		assertClientAtVector(client, vector1, vectorTolerance);
	}

	/**
	 * Assert that the player for the given client is at the given vector.
	 * @param client The client.
	 * @param vector1 The vector.
	 * @param tolerance The tolerable difference between the client's vector and the expected vector.
	 */
	protected static void assertClientAtVector(WorldClient client, Vector2D vector1, double tolerance) {
		double distance = vector1.getDistanceToVector(client.getPlayer().getVector2D());
		Assert.assertEquals(distance, 0, tolerance,
			  "V: " + client.getPlayer().getVector2D() + " " + "V1: " + vector1);
	}

	/**
	 * Initialize and log the client in.
	 * @return The initialized, logged in client.
	 * @throws Exception If there's an exception.
	 */
	protected static WorldClient initWorldClient() throws Exception {
		String username = "TestAccount1";
		LOGGER.log(Level.INFO, "Logging into account {0}", username);
		Socket sock = new Socket("localhost", port);
		MessageInputStream in = new MessageInputStream(mr, sock.getInputStream());
		MessageOutputStream out = new MessageOutputStream(mr, sock.getOutputStream());
		MessageIO messageIO = new MessageIO(in, out, mr);
		WorldClient worldClient = new WorldClient(messageIO, username, "testPassword",
			  new File("resources/WorldTestFile.xml"));
		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		return worldClient;
	}

	/**
	 * Initialize the world and world server.
	 * @return The initialized server.
	 * @throws Exception If there's an exception.
	 */
	protected static WorldServer initWorldServer() throws Exception {
		port++;
		/* The world SQL handler used to test. */
		WorldAuthenticator worldSQLHandler = getSqlHandler();
		WorldServer worldServer;
		try
		{
			ClientListenerInit<WorldClientHandler> li = new ClientListenerInit<>(WorldClientHandler.class, port);
			ServerInit<WorldClientHandler> si = new ServerInit<>(mr, WorldServer.DEFAULT_UPS, li);
			InputStream stream = WorldServerTest.class.getResourceAsStream("/com/jenjinstudios/world/WorldFile01.xml");
			WorldDocumentReader wdr = new WorldDocumentReader(stream);
			worldServer = new WorldServer(si, worldSQLHandler, wdr);
			worldServer.blockingStart();
		} catch (BindException e)
		{
			worldServer = initWorldServer();
		}
		return worldServer;
	}

	/**
	 * Move the specified actor to within one STEP_LENGTH of the specified vector.
	 * @param serverActor The actor.
	 * @param newVector The target vector.
	 * @throws InterruptedException If there is an error blocking until the target is reached.
	 */
	protected static void moveServerActorToVector(Actor serverActor, Vector2D newVector) throws InterruptedException {
		double newAngle = serverActor.getVector2D().getAngleToVector(newVector);
		serverActor.setAngle(new Angle(serverActor.getAngle().getAbsoluteAngle(), newAngle));
		double distanceToNewVector = serverActor.getVector2D().getDistanceToVector(newVector);
		while (distanceToNewVector > vectorTolerance && (serverActor.getForcedState() == null))
		{
			Thread.sleep(10);
			distanceToNewVector = serverActor.getVector2D().getDistanceToVector(newVector);
		}
		serverActor.setAngle(serverActor.getAngle().asIdle());
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
	protected static void movePlayerToVector(WorldClient client, WorldServer server,
											 Vector2D target) throws InterruptedException
	{
		ClientPlayer clientPlayer = client.getPlayer();
		String username = client.getUsername();
		Player serverPlayer = server.getClientHandlerByUsername(username).getPlayer();
		double angle = clientPlayer.getVector2D().getAngleToVector(target);
		double dist = clientPlayer.getVector2D().getDistanceToVector(target);
		if (dist <= vectorTolerance) return;
		clientPlayer.setAngle(clientPlayer.getAngle().withRelativeAngle(angle));
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
		clientPlayer.setAngle(clientPlayer.getAngle().asIdle());
		Thread.sleep(100);
		while (!serverPlayer.getAngle().isIdle())
		{
			Thread.sleep(2);
		}
	}

	/**
	 * Assert the client and the server actor are within one vectorTolerance of each other.
	 * @param serverActor The server actor.
	 * @param clientActor The client actor.
	 */
	protected static void assertClientAndServerInSamePosition(Actor serverActor, WorldObject clientActor) {
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
		LOGGER.log(Level.INFO, "Shutting down WorldClient. Avg. ping was {0}",
			  client.getPingTracker().getAveragePingTime());
		client.shutdown();
		server.shutdown();

		File resourcesDir = new File("resources/");
		Files.deleteRecursively(resourcesDir);
	}

	/**
	 * Create a unique connection with some dummy data that we can test on.
	 * @return The dummy connection.
	 * @throws Exception If something goes wrong creating the connection.
	 */
	private static Connection createTestConnection() throws Exception {
		Class.forName("org.h2.Driver");
		String connectionUrl = "jdbc:h2:mem:jenjin_test" + connectionNumber;
		Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
		Statement statement = testConnection.createStatement();
		statement.executeUpdate("CREATE TABLE users (" +
			  "  `username` VARCHAR(16) NOT NULL," +
			  "  `password` CHAR(64) NOT NULL," +
			  "  `salt` CHAR(48) NOT NULL," +
			  "  `loggedin` TINYINT NOT NULL DEFAULT '0'," +
			  "  `xcoord` DOUBLE NOT NULL DEFAULT '0'," +
			  "  `ycoord` DOUBLE NOT NULL DEFAULT '0'," +
			  "  `zoneid` INT(11) NOT NULL DEFAULT '0'," +
			  "  PRIMARY KEY (username)" +
			  ")");
		for (int i = 1; i < 100; i++)
		{
			statement.executeUpdate(
				  "INSERT INTO users " +
						"(`username`, `password`, `salt`, `loggedin`, `xcoord`, `ycoord`, `zoneid`)" +
						" VALUES " +
						"('TestAccount" + i + "', " +
						"'650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd', " +
						"'e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3', " +
						"'0', '0', '0', '0')");
		}
		connectionNumber++;
		return testConnection;
	}

	protected static WorldAuthenticator getSqlHandler() throws Exception {
		Connection dbConnection = createTestConnection();
		return new WorldAuthenticator(dbConnection);
	}
}
