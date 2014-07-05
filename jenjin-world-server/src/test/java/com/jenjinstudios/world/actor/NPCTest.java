package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.*;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.*;
import com.jenjinstudios.world.WorldServerTest;

/**
 * Test the NPC class.
 * @author Caleb Brinkman
 */
public class NPCTest extends WorldServerTest
{
	/**
	 * Test NPC movement.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public static void testNPCMovement() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer();
		WorldClient client = WorldServerTest.initWorldClient();
		ClientPlayer clientPlayer = client.getPlayer();
		NPC testNPC = new NPC("TestNPC");
		Location target = server.getWorld().getZone(0).getLocationOnGrid(0, 0);
		testNPC.setVector2D(20, 5);
		server.getWorld().addObject(testNPC);
		testNPC.plotPath(target);
		double distance = testNPC.getVector2D().getDistanceToVector(LocationUtil.getCenter(target));
		while (distance >= WorldServerTest.vectorTolerance)
		{
			distance = testNPC.getVector2D().getDistanceToVector(LocationUtil.getCenter(target));
			Thread.sleep(10);
		}
		WorldObject clientNPC = clientPlayer.getVisibleObjects().get(testNPC.getId());
		distance = testNPC.getVector2D().getDistanceToVector(clientNPC.getVector2D());
		Assert.assertEquals(distance, 0, WorldServerTest.vectorTolerance);

		// Make sure the NPC is in the same place.
		Thread.sleep(100);
		WorldServerTest.assertClientAndServerInSamePosition(testNPC, clientNPC);
		WorldServerTest.tearDown(client, server);
	}

	/**
	 * Test the path finding capability.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 30000)
	public void testPath() throws Exception {
		World world = new World();
		NPC npc = new NPC("Nearly Passable Cormorant");
		Location startLocation = world.getZone(0).getLocationOnGrid(3, 3);
		Location targetLocation = world.getZone(0).getLocationOnGrid(5, 7);
		npc.setVector2D(LocationUtil.getCenter(startLocation));

		world.addObject(npc);
		npc.plotPath(targetLocation);

		double distance = npc.getVector2D().getDistanceToVector(new Vector2D(45, 45));
		while (distance > WorldServerTest.vectorTolerance)
		{
			Thread.sleep(5);
			updateWorld(world, 1);
			distance = npc.getVector2D().getDistanceToVector(new Vector2D(45, 45));
		}

		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 55));
		while (distance > WorldServerTest.vectorTolerance)
		{
			Thread.sleep(5);
			updateWorld(world, 1);
			distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 55));
		}

		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 65));
		while (distance > WorldServerTest.vectorTolerance)
		{
			Thread.sleep(10);
			updateWorld(world, 1);
			distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 65));
		}

		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 75));
		while (distance > WorldServerTest.vectorTolerance)
		{
			Thread.sleep(10);
			updateWorld(world, 1);
			distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 75));
		}

		for (int i = 0; i < 100; i++)
		{
			updateWorld(world, 10);
			Thread.sleep(1);
		}
		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 75));
		Assert.assertEquals(0, distance, WorldServerTest.vectorTolerance);
	}

	/**
	 * Update the world the given number of times.
	 * @param world The world to update.
	 * @param num The number of times to update the world.
	 */
	private static void updateWorld(World world, int num) {
		for (int i = 0; i < num; i++)
		{
			world.update();
		}
	}
}
