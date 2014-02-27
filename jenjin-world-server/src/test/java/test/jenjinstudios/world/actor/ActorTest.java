package test.jenjinstudios.world.actor;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test the Actor class.
 * @author Caleb Brinkman
 */
public class ActorTest
{
	/** The World used to test the actor. */
	private World world;

	/**
	 * Set up the test.
	 * @throws Exception If there's an exception.
	 */
	@BeforeMethod
	public void setUp() throws Exception {
		world = new World();
	}

	/**
	 * Test adding a move state.
	 * @throws Exception If there's an exception.
	 */
	@Test
	//@Ignore
	public void testAddMoveState() throws Exception
	{
		Actor actor = new Actor("TestActor");

		world.addObject(actor);
		Assert.assertEquals(1, world.getObjectCount());

		actor.setVector2D(new Vector2D(0, 0));
		actor.addMoveState(new MoveState(MoveState.FRONT, 10, 0));
		actor.addMoveState(new MoveState(MoveState.BACK, 10, 0));
		actor.addMoveState(new MoveState(MoveState.IDLE, 10, 0));
		actor.addMoveState(new MoveState(MoveState.FRONT_LEFT, 10, 0));
		actor.addMoveState(new MoveState(MoveState.IDLE, 10, 0));

		updateWorld(10);
		Assert.assertEquals(0, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals(0, actor.getVector2D().getYCoordinate(), 0);
		updateWorld(10);
		Assert.assertEquals(2.0, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals(0, actor.getVector2D().getYCoordinate(), 0);
		updateWorld(10);
		Assert.assertEquals(0, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals(0, actor.getVector2D().getYCoordinate(), 0);
		updateWorld(10);
		Assert.assertEquals(0, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals(0, actor.getVector2D().getYCoordinate(), 0);
		updateWorld(10);
		Assert.assertEquals(1.414, actor.getVector2D().getXCoordinate(), 0.001);
		Assert.assertEquals(1.414, actor.getVector2D().getYCoordinate(), 0.001);
		updateWorld(10);
		Assert.assertEquals(1.414, actor.getVector2D().getXCoordinate(), 0);
		Assert.assertEquals(1.414, actor.getVector2D().getYCoordinate(), 0);

		world.removeObject(actor);
		Assert.assertEquals(0, world.getObjectCount());
	}

	/**
	 * Test the force-idle functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testForceIdle() throws Exception {
		Actor player = new Actor("Player");
		player.setVector2D(new Vector2D(1.0, 0));

		world.addObject(player);

		// first we move left a single step
		MoveState stepState = new MoveState(MoveState.BACK, 0, 0);
		player.addMoveState(stepState);

		updateWorld(7);
		Assert.assertEquals(new Vector2D(0, 0), player.getVector2D());
	}

	/**
	 * Update the world the given number of times.
	 * @param num The number of times to update the world.
	 */
	private void updateWorld(int num) {
		for (int i = 0; i < num; i++)
			world.update();
	}
}
