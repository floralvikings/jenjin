package test.jenjinstudios.world;

import com.jenjinstudios.util.FileUtil;
import com.jenjinstudios.world.*;
import com.jenjinstudios.world.io.WorldFileReader;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.sql.WorldSQLHandler;
import com.jenjinstudios.world.state.MoveState;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.InputStream;
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
	private static int testAccountNumber = 0;
	/** The port used to listen and connect. */
	private static int port = WorldServer.DEFAULT_PORT;

	// Server fields
	/** The world server used to test. */
	private WorldServer worldServer;
	/** The world used for testing. */
	private World world;
	/** The server-side actor representing the player. */
	private Actor serverPlayer;

	// Client fields
	/** The world client used to test. */
	private WorldClient worldClient;
	/** The client-side player used for testing. */
	private ClientPlayer clientPlayer;
	/**
	 * The tolerance for distance between a the client and server positions of an actor. This is roughly how much an
	 * actor should move during an update (assuming default UPS, which these tests do).  This means that the client
	 * and server can have about one update's worth of discrepancy between them before the tests fail.  This is
	 * intended to avoid spurious test failures that could be caused by unforeseen lag on one of the threads.
	 */
	private static final double vectorTolerance = (Actor.MOVE_SPEED / (double) WorldServer.DEFAULT_UPS);

	/**
	 * Construct the test.
	 * @throws Exception If there's an Exception.
	 */
	@BeforeClass
	public static void construct() throws Exception {
		InputStream configFile = WorldServerTest.class.getResourceAsStream("/test/jenjinstudios/logger.properties");
		LogManager.getLogManager().readConfiguration(configFile);
	}

	/**
	 * Make the client player stay idle for the given number of steps.
	 * @param i The number of steps.
	 * @param clientPlayer The client player.
	 * @throws InterruptedException If there's an issue waiting for the player to be idle for the given number of steps.
	 */
	private static void idleClientPlayer(int i, ClientPlayer clientPlayer) throws InterruptedException {
		clientPlayer.setNewRelativeAngle(MoveState.IDLE);
		while (clientPlayer.getRelativeAngle() != MoveState.IDLE || clientPlayer.getStepsTaken() < i) {
			Thread.sleep(2);
		}
	}

	/**
	 * Move the specified actor to within one STEP_LENGTH of the specified vector.
	 * @param serverActor The actor.
	 * @param newVector The target vector.
	 * @throws InterruptedException If there is an error blocking until the target is reached.
	 */
	private static void moveServerActorToVector(Actor serverActor, Vector2D newVector) throws InterruptedException {
		int stepsTaken = serverActor.getStepsTaken();
		double newAngle = serverActor.getVector2D().getAngleToVector(newVector);
		MoveState newState = new MoveState(newAngle, stepsTaken, 0);
		serverActor.addMoveState(newState);
		double distanceToNewVector = serverActor.getVector2D().getDistanceToVector(newVector);
		while (distanceToNewVector > vectorTolerance && !serverActor.isForcedState()) {
			Thread.sleep(10);
			distanceToNewVector = serverActor.getVector2D().getDistanceToVector(newVector);
		}
		MoveState idleState = new MoveState(MoveState.IDLE, serverActor.getStepsTaken(), 0);
		serverActor.addMoveState(idleState);
		Thread.sleep(10);
	}

	/**
	 * Move the client and server player to the given vector, by initiating the move client-side.  Also sends a ping to the
	 * server with each sleep cycle.
	 * @param newVector The vector to which to move.
	 * @param client The client.
	 * @param serverPlayer The server player.
	 * @throws InterruptedException If there's an exception.
	 */
	private static void moveClientPlayerTowardVector(Vector2D newVector, WorldClient client, Actor serverPlayer) throws InterruptedException {
		ClientPlayer clientPlayer = client.getPlayer();
		// Make sure not to send multiple states during the same update.
		idleClientPlayer(1, clientPlayer);
		double newAngle = clientPlayer.getVector2D().getAngleToVector(newVector);
		clientPlayer.setNewRelativeAngle(newAngle);
		double targetDistance = clientPlayer.getVector2D().getDistanceToVector(newVector);
		while (targetDistance >= vectorTolerance && !clientPlayer.isForcedState()) {
			Thread.sleep(2);
			targetDistance = clientPlayer.getVector2D().getDistanceToVector(newVector);
		}
		int stepsToIdle = Math.abs(clientPlayer.getStepsTaken() - serverPlayer.getStepsTaken()) * 5;
		idleClientPlayer(stepsToIdle, clientPlayer);
	}

	/**
	 * Set up the client and server.
	 * @throws Exception If there's an exception.
	 */
	@BeforeMethod
	public void setUp() throws Exception {
		testAccountNumber++;
		port++;
		initWorldServer();
		initWorldClient();
	}

	/**
	 * Tear down the client and server.
	 * @throws Exception If there's an exception.
	 */
	@AfterMethod
	public void tearDown() throws Exception {
		serverPlayer.setVector2D(new Vector2D(0, 0));
		worldClient.sendBlockingLogoutRequest();
		LOGGER.log(Level.INFO, "Shutting down WorldClient. Avg. ping was {0}", worldClient.getAveragePingTime());
		worldClient.shutdown();

		worldServer.shutdown();

		File resourcesDir = new File("resources/");
		FileUtil.deleteRecursively(resourcesDir);
	}

	/**
	 * Test the actor visibility after player and actor movement.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public void testActorVisibility() throws Exception {
		double visibilityEdge = Location.SIZE * (SightedObject.VIEW_RADIUS + 1);
		Vector2D serverActorStartPosition = new Vector2D(0, visibilityEdge + 1);
		Vector2D serverActorTargetPosition = new Vector2D(0, visibilityEdge - 1);
		Actor serverActor = new Actor("TestActor");
		serverActor.setVector2D(serverActorStartPosition);
		world.addObject(serverActor);

		LOGGER.log(Level.INFO, "Moving serverActor to {0}", serverActorTargetPosition);
		moveServerActorToVector(serverActor, serverActorTargetPosition);

		LOGGER.log(Level.INFO, "Asserting that clientPlayer can see serverPlayer");
		WorldObject clientActor = worldClient.getPlayer().getVisibleObjects().get(serverActor.getId());
		Assert.assertEquals(1, worldClient.getPlayer().getVisibleObjects().size());
		Assert.assertNotNull(clientActor);
		Thread.sleep(100);
		assertClientAndServerInSamePosition(serverActor, clientActor);

		LOGGER.log(Level.INFO, "Moving serverActor out of visible range.");
		moveServerActorToVector(serverActor, serverActorStartPosition);
		Assert.assertEquals(0, worldClient.getPlayer().getVisibleObjects().size());

		LOGGER.log(Level.INFO, "Moving clientPlayer into visible range.");
		moveClientPlayerTowardVector(new Vector2D(0, Location.SIZE + 1), worldClient, serverPlayer);
		Assert.assertEquals(1, worldClient.getPlayer().getVisibleObjects().size());
		clientActor = worldClient.getPlayer().getVisibleObjects().get(serverActor.getId());
		assertClientAndServerInSamePosition(serverActor, clientActor);

		LOGGER.log(Level.INFO, "Moving clientPlayer back to origin.");
		moveClientPlayerTowardVector(Vector2D.ORIGIN, worldClient, serverPlayer);
		Assert.assertEquals(0, worldClient.getPlayer().getVisibleObjects().size());
	}

	private void assertClientAndServerInSamePosition(Actor serverActor, WorldObject clientActor) {
		double distance = serverActor.getVector2D().getDistanceToVector(clientActor.getVector2D());
		Assert.assertEquals(distance, 0, vectorTolerance);
	}

	/**
	 * Test the state-forcing functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public void testForcedStateFromEdge() throws Exception {
		LOGGER.log(Level.INFO, "Attempting to move clientPlayer off edge of world.");
		moveClientPlayerTowardVector(new Vector2D(-1.0, 0), worldClient, serverPlayer);
		//moveClientPlayerTowardVector(new Vector2D(1, 0), worldClient, serverPlayer);
		Assert.assertFalse(clientPlayer.isForcedState());
		assertClientAndServerInSamePosition(serverPlayer, clientPlayer);
	}

	/**
	 * Test the state forcing functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public void testForcedState() throws Exception {
		moveClientPlayerTowardVector(new Vector2D(0.0, 0.2), worldClient, serverPlayer);
		moveClientPlayerTowardVector(new Vector2D(0.0, -0.4), worldClient, serverPlayer);
		idleClientPlayer(5, clientPlayer);
		Assert.assertEquals(clientPlayer.getVector2D(), serverPlayer.getVector2D());
	}

	/**
	 * Test basic movement.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public void testMovement() throws Exception {
		Vector2D targetVector = new Vector2D(3.956, 3.7468);
		moveClientPlayerTowardVector(targetVector, worldClient, serverPlayer);
	}

	/**
	 * Test repeatedly forcing client.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public void testRepeatedForcedState() throws Exception {
		moveClientPlayerTowardVector(new Vector2D(.5, .5), worldClient, serverPlayer);
		moveClientPlayerTowardVector(new Vector2D(-1, -1), worldClient, serverPlayer);
		moveClientPlayerTowardVector(new Vector2D(.5, .5), worldClient, serverPlayer);
		moveClientPlayerTowardVector(new Vector2D(-1, -1), worldClient, serverPlayer);
		moveClientPlayerTowardVector(new Vector2D(.5, .5), worldClient, serverPlayer);
	}

	/**
	 * Test movement to various random vectors.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public void testRandomMovement() throws Exception {
		idleClientPlayer(1, clientPlayer);
		int maxCoordinate = 3;
		for (int i = 0; i < 3; i++) {
			double randomX = MathUtil.round(java.lang.Math.random() * maxCoordinate, 4);
			double randomY = MathUtil.round(java.lang.Math.random() * maxCoordinate, 4);
			Vector2D random = new Vector2D(randomX, randomY);
			moveClientPlayerTowardVector(random, worldClient, serverPlayer);
			double distance = clientPlayer.getVector2D().getDistanceToVector(serverPlayer.getVector2D());
			Assert.assertEquals(0, distance, vectorTolerance);
		}
	}

	/**
	 * Test attempting to walk into a "blocked" location.
	 * @throws Exception If there's an Exception.
	 */
	@Test(timeOut = 10000)
	public void testAttemptBlockedLocation() throws Exception {
		Vector2D vector1 = new Vector2D(15, 0);
		Vector2D attemptedVector2 = new Vector2D(15, 15);
		Vector2D actualVector2 = new Vector2D(15, 10);
		Vector2D vector3 = new Vector2D(15, 5);
		Vector2D vector4 = new Vector2D(9, 9);
		Vector2D vector5 = new Vector2D(9, 11);
		Vector2D attemptedVector6 = new Vector2D(15, 11);
		Vector2D actualVector6 = new Vector2D(9.8, 11);
		Vector2D attemptedVector7 = new Vector2D(15, 15);
		Vector2D actualVector7 = new Vector2D(9.8, 11);

		// Move to (35, 0)
		moveClientPlayerTowardVector(vector1, worldClient, serverPlayer);
		assertClientAtVector(vector1);

		// Attempt to move to (35, 35)
		// This attempt should be forced to stop one step away from
		moveClientPlayerTowardVector(attemptedVector2, worldClient, serverPlayer);
		assertClientAtVector(actualVector2);

		moveClientPlayerTowardVector(vector3, worldClient, serverPlayer);
		assertClientAtVector(vector3);

		moveClientPlayerTowardVector(vector4, worldClient, serverPlayer);
		assertClientAtVector(vector4);

		moveClientPlayerTowardVector(vector5, worldClient, serverPlayer);
		assertClientAtVector(vector5);

		moveClientPlayerTowardVector(vector5, worldClient, serverPlayer);
		assertClientAtVector(vector5);

		moveClientPlayerTowardVector(attemptedVector6, worldClient, serverPlayer);
		assertClientAtVector(actualVector6);

		moveClientPlayerTowardVector(attemptedVector7, worldClient, serverPlayer);
		assertClientAtVector(actualVector7);
	}

	private void assertClientAtVector(Vector2D vector1) {
		double distance = vector1.getDistanceToVector(clientPlayer.getVector2D());
		Assert.assertEquals(distance, 0, vectorTolerance,
				"V: " + clientPlayer.getVector2D() + " " + "V1: " + vector1);
	}

	/**
	 * Test NPC movement.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public void testNPCMovement() throws Exception {
		NPC testNPC = new NPC("TestNPC");
		Location target = world.getZone(0).getLocationOnGrid(0, 0);
		testNPC.setVector2D(20, 5);
		world.addObject(testNPC);
		testNPC.plotPath(target);
		double distance = testNPC.getVector2D().getDistanceToVector(target.getCenter());
		while (distance >= vectorTolerance) {
			distance = testNPC.getVector2D().getDistanceToVector(target.getCenter());
			Thread.sleep(10);
		}
		WorldObject clientNPC = clientPlayer.getVisibleObjects().get(testNPC.getId());
		distance = testNPC.getVector2D().getDistanceToVector(clientNPC.getVector2D());
		Assert.assertEquals(distance, 0, vectorTolerance);

		// Make sure the NPC is in the same place.
		Thread.sleep(100);
		assertClientAndServerInSamePosition(testNPC, clientNPC);
	}

	/**
	 * Test logging the player into and out of the world, including updating coordinates.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLoginLogout() throws Exception {
		testAccountNumber++;
		WorldSQLHandler worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user",
				"jenjin_password");

		Assert.assertTrue(worldSQLHandler.isConnected());

		Actor player = worldSQLHandler.logInPlayer("TestAccount" + testAccountNumber, "testPassword");
		Vector2D origin = player.getVector2D();
		Vector2D secondVector = new Vector2D(50, 50);

		Assert.assertEquals(origin, player.getVector2D());

		player.setVector2D(secondVector);
		Assert.assertTrue(worldSQLHandler.logOutPlayer(player));

		player = worldSQLHandler.logInPlayer("TestAccount" + testAccountNumber, "testPassword");
		Assert.assertEquals(secondVector, player.getVector2D());

		player.setVector2D(origin);
		Assert.assertTrue(worldSQLHandler.logOutPlayer(player));

		player = worldSQLHandler.logInPlayer("TestAccount" + testAccountNumber, "testPassword");
		Assert.assertEquals(origin, player.getVector2D());

		Assert.assertTrue(worldSQLHandler.logOutPlayer(player));
	}

	/**
	 * Initialize and log the client in.
	 * @throws Exception If there's an exception.
	 */
	private void initWorldClient() throws Exception {
		String user = "TestAccount" + testAccountNumber;
		LOGGER.log(Level.INFO, "Logging into account {0}", user);
		worldClient = new WorldClient(new File("resources/WorldTestFile.xml"), "localhost", port, user, "testPassword");
		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();

		/* The WorldClientHandler used to test. */
		WorldClientHandler worldClientHandler = worldServer.getClientHandlerByUsername(worldClient.getUsername());
		clientPlayer = worldClient.getPlayer();
		serverPlayer = worldClientHandler.getPlayer();
	}

	/**
	 * Initialize the world and world server.
	 * @throws Exception If there's an exception.
	 */
	private void initWorldServer() throws Exception {
		/* The world SQL handler used to test. */
		WorldSQLHandler worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user", "jenjin_password");
		worldServer = new WorldServer(new WorldFileReader(getClass().getResourceAsStream("/test/jenjinstudios/world/WorldFile01.xml")),
				WorldServer.DEFAULT_UPS, port, WorldClientHandler.class, worldSQLHandler);
		world = worldServer.getWorld();
		worldServer.blockingStart();
	}

}
