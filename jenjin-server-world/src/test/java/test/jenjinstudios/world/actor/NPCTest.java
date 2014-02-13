package test.jenjinstudios.world.actor;

import com.jenjinstudios.world.*;
import com.jenjinstudios.world.math.Vector2D;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the NPC class.
 * @author Caleb Brinkman
 */
public class NPCTest
{
	/** The NPC used for testing. */
	NPC npc = new NPC("Nearly Passable Cormorant");
	/** The World used to test the actor. */
	private World world;

	/**
	 * Set up the test.
	 * @throws Exception If there's an exception.
	 */
	@Before
	public void setUp() throws Exception {
		world = new World();
	}

	/**
	 * Test the path finding capability.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testPath() throws Exception {
		Location startLocation = world.getZones()[0].getLocationOnGrid(3, 3);
		Location targetLocation = world.getZones()[0].getLocationOnGrid(5, 7);
		npc.setVector2D(startLocation.getCenter());

		world.addObject(npc);
		npc.plotPath(targetLocation);

		updateWorld(71);
		double distance = npc.getVector2D().getDistanceToVector(new Vector2D(45, 45));
		Assert.assertEquals(0, distance, Actor.STEP_LENGTH);

		updateWorld(71);
		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 55));
		Assert.assertEquals(0, distance, Actor.STEP_LENGTH);

		updateWorld(50);
		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 65));
		Assert.assertEquals(0, distance, Actor.STEP_LENGTH);

		updateWorld(50);
		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 75));
		Assert.assertEquals(0, distance, Actor.STEP_LENGTH);

		updateWorld(1000);
		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 75));
		Assert.assertEquals(0, distance, Actor.STEP_LENGTH);
	}

	/**
	 * Update the world the given number of times.
	 * @param num The number of times to update the world.
	 */
	private void updateWorld(int num) {
		for (int i = 0; i < num; i++)
		{
			world.update();
		}
	}
}
