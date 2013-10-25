package test.jenjinstudios.world.actor;

import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.state.MoveState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Test the Actor class.
 *
 * @author Caleb Brinkman
 */
public class ActorTest
{
	/** The World used to test the actor. */
	private World world;

	/**
	 * Set up the test.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Before
	public void setUp() throws Exception
	{
		world = new World();
	}

	/**
	 * Test adding a move state.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	@Ignore
	public void testAddMoveState() throws Exception
	{
		Actor actor = new Actor();

		world.addObject(actor);
		Assert.assertEquals(1, world.getObjectCount());

		actor.setVector2D(0, 0);
		actor.addMoveState(new MoveState(MoveState.FRONT, 10, 0));
		actor.addMoveState(new MoveState(MoveState.BACK, 10, 0));
		actor.addMoveState(new MoveState(MoveState.IDLE, 10, 0));
		actor.addMoveState(new MoveState(MoveState.FRONT_LEFT, 10, 0));
		actor.addMoveState(new MoveState(MoveState.IDLE, 10, 0));

		updateWorld(10);
		Assert.assertEquals("State 1 X: ", 0, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals("State 1 Z: ", 0, actor.getVector2D().getZCoordinate(), 0);
		updateWorld(10);
		Assert.assertEquals("State 2 X: ", 50, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals("State 2 Z: ", 0, actor.getVector2D().getZCoordinate(), 0);
		updateWorld(10);
		Assert.assertEquals("State 3 X: ", 0, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals("State 3 Z: ", 0, actor.getVector2D().getZCoordinate(), 0);
		updateWorld(10);
		Assert.assertEquals("State 4 X: ", 0, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals("State 4 Z: ", 0, actor.getVector2D().getZCoordinate(), 0);
		updateWorld(10);
		Assert.assertEquals("State 5 X: ", 35.355, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals("State 5 Z: ", 35.355, actor.getVector2D().getZCoordinate(), 0);
		updateWorld(10);
		Assert.assertEquals("State 6 X: ", 35.355, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals("State 6 Z: ", 35.355, actor.getVector2D().getZCoordinate(), 0);

		world.removeObject(actor);
		Assert.assertEquals(0, world.getObjectCount());
	}

	/**
	 * Test the visible objects method.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testVisibleObjects() throws Exception
	{
		Actor player = new Actor("Player");
		player.setVector2D(40, 40);

		WorldObject object01 = new WorldObject();
		object01.setVector2D(0, 0);

		WorldObject object02 = new WorldObject();
		object02.setVector2D(30, 30);

		WorldObject object03 = new WorldObject();
		object03.setVector2D(60, 60);

		WorldObject object04 = new WorldObject();
		object04.setVector2D(90, 90); // Should be invisible

		WorldObject object05 = new WorldObject();
		object05.setVector2D(0, 90); // Should be invisible

		world.addObject(player);
		world.addObject(object01);
		world.addObject(object02);
		world.addObject(object03);
		world.addObject(object04);
		world.addObject(object05);

		// Have to update... duh.
		world.update();

		ArrayList<WorldObject> playerVisible = player.getVisibleObjects();

		Assert.assertTrue(playerVisible.contains(object01));

		Assert.assertTrue(playerVisible.contains(object02));

		Assert.assertTrue(playerVisible.contains(object03));

		Assert.assertFalse(playerVisible.contains(object04));

		Assert.assertFalse(playerVisible.contains(object05));

		world.removeObject(player);
		world.removeObject(object01);
		world.removeObject(object02);
		world.removeObject(object03);
		world.removeObject(object04);
		world.removeObject(object05);
	}

	/**
	 * Test the force-idle functionality.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	@Ignore
	public void testForceIdle() throws Exception
	{
		Actor player = new Actor("Player");
		player.setVector2D(20, 0);

		world.addObject(player);

		// first we move right a single step
		MoveState stepState = new MoveState(MoveState.BACK, 0, 0);
		player.addMoveState(stepState);

		updateWorld(7); // position should correct to -5,0
		Assert.assertEquals(new Vector2D(-5, 0), player.getVector2D());
	}

	/**
	 * Update the world the given number of times.
	 *
	 * @param num The number of times to update the world.
	 */
	private void updateWorld(int num)
	{
		for (int i = 0; i < num; i++)
			world.update();
	}
}
