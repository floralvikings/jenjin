package test.jenjinstudios.world;

import com.jenjinstudios.world.ClientPlayer;
import com.jenjinstudios.world.WorldClient;
import com.jenjinstudios.world.WorldServer;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ClientPlayerTest
{

	/**
	 * Set up the client and server.
	 * @throws Exception If there's an exception.
	 */
	@BeforeMethod
	public static void setUp() throws Exception {
		WorldServerTest.setUp();
	}

	/**
	 * Test the movement of the client-side player.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public void testClientPlayerMovement() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer(WorldServerTest.port);
		WorldClient client = WorldServerTest.initWorldClient(WorldServerTest.port);
		ClientPlayer clientPlayer = client.getPlayer();
		Vector2D target = new Vector2D(10, 0);
		WorldServerTest.movePlayerToVector(client, server, target);
		double dist = target.getDistanceToVector(clientPlayer.getVector2D());
		Assert.assertEquals(dist, 0, WorldServerTest.vectorTolerance);
		WorldServerTest.tearDown(client, server);
	}
}
