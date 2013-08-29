package test.jenjinstudios.jgsf.world.actor;

import com.jenjinstudios.jgcf.world.math.Vector2D;
import com.jenjinstudios.jgcf.world.state.MoveDirection;
import com.jenjinstudios.jgcf.world.state.MoveState;
import com.jenjinstudios.jgsf.world.World;
import com.jenjinstudios.jgsf.world.WorldServer;
import com.jenjinstudios.jgsf.world.actor.Actor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the Actor class.
 *
 * @author Caleb Brinkman
 */
public class ActorTest
{
	/** The World used to test the actor. */
	private World world;
	/** The server used to test the actor. */
	private WorldServer server;

	/**
	 * Set up the test.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Before
	public void setUp() throws Exception
	{
		world = new World();
		server = new WorldServer(world);
		server.blockingStart();
	}

	/**
	 * Tear down the test.
	 *
	 * @throws Exception If there's an exception.
	 */
	@After
	public void tearDown() throws Exception
	{
		server.shutdown();
	}

	/**
	 * Test adding a move state.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testAddMoveState() throws Exception
	{
		Actor actor = new Actor();
		actor.setVector2D(0, 0);
		actor.addMoveState(new MoveState(MoveDirection.FRONT, 10, 0));
		actor.addMoveState(new MoveState(MoveDirection.BACK, 10, 0));
		actor.addMoveState(new MoveState(MoveDirection.IDLE, 10, 0));
		actor.addMoveState(new MoveState(MoveDirection.BACK_LEFT, 10, 0));
		actor.addMoveState(new MoveState(MoveDirection.IDLE, 10, 0));
		world.addObject(actor);
		Assert.assertEquals("State 1: ", new Vector2D(0, 0), actor.getVector2D());
		Thread.sleep(server.PERIOD * 10);
		Assert.assertEquals("State 2: ", new Vector2D(50, 0), actor.getVector2D());
		Thread.sleep(server.PERIOD * 10);
		Assert.assertEquals("State 3: ", new Vector2D(0, 0), actor.getVector2D());
		Thread.sleep(server.PERIOD * 10);
		Assert.assertEquals("State 4: ", new Vector2D(0, 0), actor.getVector2D());
		Thread.sleep(server.PERIOD * 10);
		Assert.assertEquals("State 5: ", new Vector2D(-35.355, 35.355), actor.getVector2D());
		Thread.sleep(server.PERIOD * 10);
		Assert.assertEquals("State 6: ", new Vector2D(-35.355, 35.355), actor.getVector2D());
	}

	/** Test the login and logout functionality of the WorldServer. */
	@Test
	public void testLoginLogout()
	{

	}
}
