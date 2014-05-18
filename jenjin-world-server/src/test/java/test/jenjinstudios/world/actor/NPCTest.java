package test.jenjinstudios.world.actor;

import com.jenjinstudios.world.*;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * Test the NPC class.
 * @author Caleb Brinkman
 */
public class NPCTest
{
	/** The NPC used for testing. */
	private final NPC npc = new NPC("Nearly Passable Cormorant");
	/** The World used to test the actor. */
	private World world;
	/**
	 * The tolerance for distance between a the client and server positions of an actor. This allows for the client and
	 * server to have a single update of descrepancy between them.
	 */
	private static final double vectorTolerance = (Actor.MOVE_SPEED / (double) WorldServer.DEFAULT_UPS);

	/**
	 * Set up the test.
	 * @throws Exception If there's an exception.
	 */
	@BeforeMethod
	public void setUp() throws Exception {
		world = new World();
	}

	/**
	 * Test the path finding capability.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 30000)
	public void testPath() throws Exception {
		Location startLocation = world.getZone(0).getLocationOnGrid(3, 3);
		Location targetLocation = world.getZone(0).getLocationOnGrid(5, 7);
		npc.setVector2D(startLocation.getCenter());

		world.addObject(npc);
		npc.plotPath(targetLocation);

		double distance = npc.getVector2D().getDistanceToVector(new Vector2D(45, 45));
		while(distance > vectorTolerance) {
			Thread.sleep(5);
			updateWorld(1);
			distance = npc.getVector2D().getDistanceToVector(new Vector2D(45, 45));
		}

		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 55));
		while(distance > vectorTolerance) {
			Thread.sleep(5);
			updateWorld(1);
			distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 55));
		}

		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 65));
		while(distance > vectorTolerance) {
			Thread.sleep(10);
			updateWorld(1);
			distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 65));
		}

		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 75));
		while(distance > vectorTolerance) {
			Thread.sleep(10);
			updateWorld(1);
			distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 75));
		}

		for(int i=0; i< 100; i++) {
			updateWorld(10);
			Thread.sleep(1);
		}
		distance = npc.getVector2D().getDistanceToVector(new Vector2D(55, 75));
		Assert.assertEquals(0, distance, vectorTolerance);
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
