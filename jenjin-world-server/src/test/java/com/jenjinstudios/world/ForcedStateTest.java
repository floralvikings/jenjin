package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public class ForcedStateTest extends WorldServerTest
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ForcedStateTest.class.getName());

	/**
	 * Test the state forcing functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public static void testForcedState() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer(WorldServerTest.port);
		WorldClient client = WorldServerTest.initWorldClient(WorldServerTest.port);
		Player serverPlayer = server.getClientHandlerByUsername(client.getUsername()).getPlayer();
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(0.0, 1.0));
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(0.0, -0.4));

		double distance = serverPlayer.getVector2D().getDistanceToVector(new Vector2D(0, 0.2));
		Assert.assertEquals(distance, 0, WorldServerTest.vectorTolerance);

		WorldServerTest.tearDown(client, server);
	}

	/**
	 * Test attempting to walk into a "blocked" location.
	 * @throws Exception If there's an Exception.
	 */
	@Test(timeOut = 10000)
	public static void testAttemptBlockedLocation() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer(WorldServerTest.port);
		WorldClient client = WorldServerTest.initWorldClient(WorldServerTest.port);
		Vector2D vector1 = new Vector2D(15, 0);
		Vector2D attemptedVector2 = new Vector2D(15, 15);
		Vector2D actualVector2 = new Vector2D(15, 10);
		Vector2D vector3 = new Vector2D(15, 5);
		Vector2D vector4 = new Vector2D(9, 9);
		Vector2D vector5 = new Vector2D(9, 11);
		Vector2D attemptedVector6 = new Vector2D(15, 11);
		Vector2D actualVector6 = new Vector2D(9.8, 11);
		Vector2D attemptedVector7 = new Vector2D(15, 15);
		Vector2D actualVector7 = new Vector2D(10, 11);

		// Move to (35, 0)
		WorldServerTest.movePlayerToVector(client, server, vector1);
		WorldServerTest.assertClientAtVector(client, vector1);

		// Attempt to move to (35, 35)
		// This attempt should be forced to stop one step away from
		WorldServerTest.movePlayerToVector(client, server, attemptedVector2);
		WorldServerTest.assertClientAtVector(client, actualVector2);

		WorldServerTest.movePlayerToVector(client, server, vector3);
		WorldServerTest.assertClientAtVector(client, vector3);

		WorldServerTest.movePlayerToVector(client, server, vector4);
		WorldServerTest.assertClientAtVector(client, vector4);

		WorldServerTest.movePlayerToVector(client, server, vector5);
		WorldServerTest.assertClientAtVector(client, vector5);

		WorldServerTest.movePlayerToVector(client, server, vector5);
		WorldServerTest.assertClientAtVector(client, vector5);

		WorldServerTest.movePlayerToVector(client, server, attemptedVector6);
		WorldServerTest.assertClientAtVector(client, actualVector6);

		WorldServerTest.movePlayerToVector(client, server, attemptedVector7);
		WorldServerTest.assertClientAtVector(client, actualVector7);
		WorldServerTest.tearDown(client, server);
	}

	/**
	 * Test repeatedly forcing client.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public static void testRepeatedForcedState() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer(WorldServerTest.port);
		WorldClient client = WorldServerTest.initWorldClient(WorldServerTest.port);
		ClientPlayer clientPlayer = client.getPlayer();
		Player serverPlayer = server.getClientHandlerByUsername(client.getUsername()).getPlayer();
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(.5, .5));
		double distance = clientPlayer.getVector2D().getDistanceToVector(serverPlayer.getVector2D());
		Assert.assertEquals(0, distance, WorldServerTest.vectorTolerance, " Server Vector: " + serverPlayer.getVector2D() +
				serverPlayer.getRelativeAngle() +
				" Client Vector: " + clientPlayer.getVector2D());
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(-1, -1));
		distance = clientPlayer.getVector2D().getDistanceToVector(serverPlayer.getVector2D());
		Assert.assertEquals(0, distance, WorldServerTest.vectorTolerance, " Server Vector: " + serverPlayer.getVector2D() +
				serverPlayer.getRelativeAngle() +
				" Client Vector: " + clientPlayer.getVector2D());
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(.5, .5));
		distance = clientPlayer.getVector2D().getDistanceToVector(serverPlayer.getVector2D());
		Assert.assertEquals(0, distance, WorldServerTest.vectorTolerance, " Server Vector: " + serverPlayer.getVector2D() +
				serverPlayer.getRelativeAngle() +
				" Client Vector: " + clientPlayer.getVector2D());
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(-1, -1));
		Assert.assertEquals(0, distance, WorldServerTest.vectorTolerance, " Server Vector: " + serverPlayer.getVector2D() +
				serverPlayer.getRelativeAngle() +
				" Client Vector: " + clientPlayer.getVector2D());
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(.5, .5));
		distance = clientPlayer.getVector2D().getDistanceToVector(serverPlayer.getVector2D());
		Assert.assertEquals(0, distance, WorldServerTest.vectorTolerance, " Server Vector: " + serverPlayer.getVector2D() +
				serverPlayer.getRelativeAngle() +
				" Client Vector: " + clientPlayer.getVector2D());
		WorldServerTest.tearDown(client, server);
	}

	/**
	 * Test the state-forcing functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public static void testForcedStateFromEdge() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer(WorldServerTest.port);
		WorldClient client = WorldServerTest.initWorldClient(WorldServerTest.port);
		ClientPlayer clientPlayer = client.getPlayer();
		Player serverPlayer = server.getClientHandlerByUsername(client.getUsername()).getPlayer();
		LOGGER.log(Level.INFO, "Attempting to move clientPlayer off edge of world.");
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(-1.0, 0));
		WorldServerTest.assertClientAndServerInSamePosition(serverPlayer, clientPlayer);
		WorldServerTest.tearDown(client, server);
	}
}
