package com.jenjinstudios.world;

import com.jenjinstudios.server.net.User;
import com.jenjinstudios.world.client.ClientPlayer;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.sql.WorldAuthenticator;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class PlayerTest extends WorldServerTest
{

	/**
	 * Test movement synchronization between client and server-side players.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public static void testSynchronizedPlayerMovement() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer();
		WorldClient client = WorldServerTest.initWorldClient();
		ClientPlayer clientPlayer = client.getPlayer();
		Player serverPlayer = server.getClientHandlerByUsername(client.getUsername()).getPlayer();
		// Start and stop the player a few times to make sure the server is keeping up.
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(4, 0));
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(8, 0));
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(10, 0));
		double dist = serverPlayer.getVector2D().getDistanceToVector(clientPlayer.getVector2D());
		Assert.assertEquals(dist, 0, WorldServerTest.vectorTolerance, "Server Vector: " +
			  serverPlayer.getVector2D() + " Client Vector: " + clientPlayer.getVector2D());
	}

	/**
	 * Test movement to various random vectors.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public static void testRandomMovement() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer();
		WorldClient client = WorldServerTest.initWorldClient();
		ClientPlayer clientPlayer = client.getPlayer();
		Player serverPlayer = server.getClientHandlerByUsername(client.getUsername()).getPlayer();
		clientPlayer.setAngle(clientPlayer.getAngle().asIdle());
		Thread.sleep(100);
		int maxCoordinate = 3;
		for (int i = 0; i < 3; i++)
		{
			double randomX = MathUtil.round(Math.random() * maxCoordinate, 4);
			double randomY = MathUtil.round(Math.random() * maxCoordinate, 4);
			Vector2D target = new Vector2D(randomX, randomY);
			WorldServerTest.movePlayerToVector(client, server, target);
			double distance = clientPlayer.getVector2D().getDistanceToVector(serverPlayer.getVector2D());
			Assert.assertEquals(0, distance, WorldServerTest.vectorTolerance, "Server Vector: " + serverPlayer
				  .getVector2D() +
				  " Client Vector: " + clientPlayer.getVector2D() + " Target Vector: " + target);
		}
		WorldServerTest.tearDown(client, server);
	}

	/**
	 * Test logging the player into and out of the world, including updating coordinates.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public static void testLoginLogout() throws Exception {
		WorldAuthenticator worldSQLHandler = getSqlHandler();

		User user = new User();
		user.setUsername("TestAccount2");
		user.setPassword("testPassword");
		Actor player = worldSQLHandler.logInPlayer(user);
		Vector2D origin = player.getVector2D();
		Vector2D secondVector = new Vector2D(50, 50);

		Assert.assertEquals(origin, player.getVector2D());

		player.setVector2D(secondVector);
		Assert.assertTrue(worldSQLHandler.logOutPlayer(player));

		player = worldSQLHandler.logInPlayer(user);
		Assert.assertEquals(secondVector, player.getVector2D());

		player.setVector2D(origin);
		Assert.assertTrue(worldSQLHandler.logOutPlayer(player));

		player = worldSQLHandler.logInPlayer(user);
		Assert.assertEquals(origin, player.getVector2D());

		Assert.assertTrue(worldSQLHandler.logOutPlayer(player));
	}

	/**
	 * Test the movement of the client-side player.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public void testClientPlayerMovement() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer();
		WorldClient client = WorldServerTest.initWorldClient();
		ClientPlayer clientPlayer = client.getPlayer();
		Vector2D target = new Vector2D(10, 0);
		WorldServerTest.movePlayerToVector(client, server, target);
		double dist = target.getDistanceToVector(clientPlayer.getVector2D());
		Assert.assertEquals(dist, 0, WorldServerTest.vectorTolerance);
		WorldServerTest.tearDown(client, server);
	}
}
