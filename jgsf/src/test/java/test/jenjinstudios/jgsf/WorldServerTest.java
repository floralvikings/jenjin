package test.jenjinstudios.jgsf;

import com.jenjinstudios.jgcf.WorldClient;
import com.jenjinstudios.jgsf.WorldServer;
import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.sql.WorldSQLHandler;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the world server.
 *
 * @author Caleb Brinkman
 */
public class WorldServerTest
{
	/**
	 * Runs a battery of tests for the WorldServer.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testWorldServer() throws Exception
	{
		// Set up the world server
		WorldSQLHandler worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user", "jenjin_password");
		WorldServer worldServer = new WorldServer();
		worldServer.setSQLHandler(worldSQLHandler);
		World world = worldServer.getWorld();
		worldServer.blockingStart();

		// Set up the client
		WorldClient worldClient = new WorldClient("localhost", WorldServer.DEFAULT_PORT, "TestAccount01", "testPassword");
		worldClient.blockingStart();

		// Test logging in
		worldClient.loginToWorld();
		Assert.assertEquals(1, world.getObjectCount());

		// Test visible actors and location.
		Actor player = worldServer.getClientHandlerByUsername("TestAccount01").getPlayer();
		Assert.assertEquals(0, player.getVisibleObjects().size());
		Assert.assertEquals(new Vector2D(0, 0), player.getVector2D());
		Assert.assertEquals(16, player.getVisibleLocations().size());

		// Add a new actor, and make sure the player can see it.
		Actor testActor01 = new Actor("TestActor01");
		testActor01.setVector2D(30, 30);
		world.addObject(testActor01);
		Thread.sleep(worldServer.PERIOD);
		Assert.assertEquals(1, player.getVisibleObjects().size());

		// Test logging out.
		worldClient.logoutOfWorld();
		Assert.assertEquals(1, world.getObjectCount());

		// Shut everything down.
		worldClient.shutdown();
		worldServer.shutdown();
	}
}
