package test.jenjinstudios.jgsf;

import com.jenjinstudios.jgcf.WorldClient;
import com.jenjinstudios.jgsf.WorldClientHandler;
import com.jenjinstudios.jgsf.WorldServer;
import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.sql.WorldSQLHandler;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.state.MoveDirection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the world server.
 *
 * @author Caleb Brinkman
 */
public class WorldServerTest
{
	/** The world server used to test. */
	private WorldServer worldServer;
	/** The world used for testing. */
	private World world;
	/** The world client used to test. */
	private WorldClient worldClient;
	/** The WorldClientHandler used to test. */
	private WorldClientHandler worldClientHandler;
	/** The player used to test. */
	private Actor player;

	/**
	 * Set up the client and server.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Before
	public void setUp() throws Exception
	{
		// Set up the world server
		WorldSQLHandler worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user", "jenjin_password");
		worldServer = new WorldServer();
		worldServer.setSQLHandler(worldSQLHandler);
		world = worldServer.getWorld();
		worldServer.blockingStart();

		// Set up the client
		worldClient = new WorldClient("localhost", WorldServer.DEFAULT_PORT, "TestAccount01", "testPassword");
		worldClient.blockingStart();

		// Test logging in
		int preLogin = world.getObjectCount();
		worldClient.loginToWorld();
		int postLogin = world.getObjectCount();
		Assert.assertEquals(preLogin + 1, postLogin);

		worldClientHandler = worldServer.getClientHandlerByUsername("TestAccount01");
		player = worldClientHandler.getPlayer();

		// Hard set the player's position to 0
		player.setVector2D(0, 0);
		worldClient.getPlayer().setVector2D(0, 0);
	}

	/**
	 * Tear down the client and server.
	 *
	 * @throws Exception If there's an exception.
	 */
	@After
	public void tearDown() throws Exception
	{
		// Hard reset the player in case movement goes wrong.
		player.setVector2D(0, 0);

		// Test logging out.
		int preLogout = world.getObjectCount();
		worldClient.logoutOfWorld();
		int postLogout = world.getObjectCount();
		Assert.assertEquals(preLogout - 1, postLogout);

		// Shut everything down.
		worldClient.shutdown();
		worldServer.shutdown();
	}

	/**
	 * Test the movement requests.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testMovement() throws Exception
	{
		// Make sure the actor moves, within reasonably accuracy accounting for time.
		Message moveForwardMessage = new Message("StateChangeRequest");
		moveForwardMessage.setArgument("direction", MoveDirection.FRONT.ordinal());
		moveForwardMessage.setArgument("angle", 0d);
		int updatesSinceLogin = (int) ((System.nanoTime() - worldClientHandler.getLoggedInTime()) / (worldServer.PERIOD * 1000000));
		moveForwardMessage.setArgument("stepsUntilChange", updatesSinceLogin);

		Message moveBackMessage = new Message("StateChangeRequest");
		moveBackMessage.setArgument("direction", MoveDirection.BACK.ordinal());
		moveBackMessage.setArgument("angle", 0d);
		moveBackMessage.setArgument("stepsUntilChange", 5);

		Message idleMessage = new Message("StateChangeRequest");
		idleMessage.setArgument("direction", MoveDirection.IDLE.ordinal());
		idleMessage.setArgument("angle", 0d);
		idleMessage.setArgument("stepsUntilChange", 5);

		worldClient.sendMessage(moveForwardMessage);
		worldClient.sendMessage(moveBackMessage);
		worldClient.sendMessage(idleMessage);

		Thread.sleep(worldServer.PERIOD * 6);
		Assert.assertEquals(25, player.getVector2D().getXCoordinate(), 5.0d);
		Thread.sleep(worldServer.PERIOD * 5);
		Assert.assertEquals(0, player.getVector2D().getXCoordinate(), 5.0d);
	}

	/**
	 * Runs a battery of tests for the WorldServer.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testWorldServer() throws Exception
	{
		// Test visible actors and location.
		Assert.assertEquals(0, player.getVisibleObjects().size());
		Assert.assertEquals(new Vector2D(0, 0), player.getVector2D());
		Assert.assertEquals(16, player.getVisibleLocations().size());

		// Add a new actor, and make sure the player can see it.
		Actor testActor01 = new Actor("TestActor01");
		testActor01.setVector2D(30, 30);
		world.addObject(testActor01);
		Thread.sleep(worldServer.PERIOD);
		Assert.assertEquals(1, player.getVisibleObjects().size());
		Thread.sleep(worldServer.PERIOD);
		Assert.assertEquals(1, worldClient.getVisibleObjects().size());

		// Now add a new Object, and make sure the player can see it.
		WorldObject testObject01 = new Actor("TestActor01");
		testObject01.setVector2D(10, 10);
		world.addObject(testObject01);
		Thread.sleep(worldServer.PERIOD);
		Assert.assertEquals(2, player.getVisibleObjects().size());
		Thread.sleep(worldServer.PERIOD);
		Assert.assertEquals(2, worldClient.getVisibleObjects().size());
	}
}
