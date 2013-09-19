package test.jenjinstudios.jgsf;

import com.jenjinstudios.jgcf.WorldClient;
import com.jenjinstudios.jgsf.WorldServer;
import com.jenjinstudios.sql.WorldSQLHandler;
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
		WorldSQLHandler worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user", "jenjin_password");
		WorldServer worldServer = new WorldServer();
		worldServer.setSQLHandler(worldSQLHandler);
		World world = worldServer.getWorld();
		worldServer.blockingStart();

		WorldClient worldClient = new WorldClient("localhost", WorldServer.DEFAULT_PORT, "TestAccount01", "testPassword");
		worldClient.blockingStart();

		worldClient.loginToWorld();

		Assert.assertEquals(1, world.getObjectCount());

		worldClient.logoutOfWorld();

		Assert.assertEquals(0, world.getObjectCount());

		worldClient.shutdown();
		worldServer.shutdown();
	}
}
