package test.jenjinstudios.jgsf;

import com.jenjinstudios.jgcf.WorldClient;
import com.jenjinstudios.jgsf.WorldClientHandler;
import com.jenjinstudios.jgsf.WorldServer;
import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.sql.WorldSQLHandler;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.ClientPlayer;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.state.MoveState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test the world server.
 *
 * @author Caleb Brinkman
 */
public class WorldServerTest
{
	// Server fields
	/** The world server used to test. */
	private WorldServer worldServer;
	/** The world SQL handler used to test. */
	private WorldSQLHandler worldSQLHandler;
	/** The WorldClientHandler used to test. */
	private WorldClientHandler worldClientHandler;
	/** The world used for testing. */
	private World world;
	/** The server-side actor representing the player. */
	private Actor serverPlayer;
	/** The server-side actor used to test visibilty functionalty. */
	private Actor serverActor;

	// Client fields
	/** The world client used to test. */
	private WorldClient worldClient;
	/** The client-side player used for testing. */
	private ClientPlayer clientPlayer;

	/**
	 * Set up the client and server.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Before
	public void setUp() throws Exception
	{
		initWorldServer();
		initWorldClient();
	}

	/**
	 * Initialize and log the client in.
	 *
	 * @throws Exception If there's an exception.
	 */
	private void initWorldClient() throws Exception
	{
		worldClient = new WorldClient("localhost", WorldServer.DEFAULT_PORT, "TestAccount01", "testPassword");
		worldClient.blockingStart();
		worldClient.sendBlockingLoginRequest();

		worldClientHandler = worldServer.getClientHandlerByUsername(worldClient.getUsername());
		clientPlayer = worldClient.getPlayer();
		serverPlayer = worldClientHandler.getPlayer();
	}

	/**
	 * Initialize the world and world server.
	 *
	 * @throws Exception If there's an exception.
	 */
	private void initWorldServer() throws Exception
	{
		worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user", "jenjin_password");
		world = new World();
		worldServer = new WorldServer(world);
		worldServer.setSQLHandler(worldSQLHandler);
		// FIXME Blocking on login request if SQL handler not set. Bad.
		worldServer.blockingStart();
	}

	/**
	 * Tear down the client and server.
	 *
	 * @throws Exception If there's an exception.
	 */
	@After
	public void tearDown() throws Exception
	{
		serverPlayer.setVector2D(0, 0);
		worldClient.sendBlockingLogoutRequest();
		worldClient.shutdown();

		worldServer.shutdown();
	}

	/**
	 * Run the battery of world tests.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testMovement() throws Exception
	{
		Vector2D randomVector = getRandomVector();

		movePlayerTowardVector(randomVector);
		assertEquals("Client and Server Coordinates", serverPlayer.getVector2D(), clientPlayer.getVector2D());

		movePlayerToOrigin();
		assertEquals("Client and Server Coordinates", serverPlayer.getVector2D(), clientPlayer.getVector2D());
	}

	/**
	 * Move the player to the origin.
	 *
	 * @throws InterruptedException If there's an exception.
	 */
	private void movePlayerToOrigin() throws InterruptedException
	{
		clientPlayer.setRelativeAngle(clientPlayer.getVector2D().getAngleToVector(Vector2D.ORIGIN));
		while (!clientPlayer.getVector2D().equals(Vector2D.ORIGIN)) { Thread.sleep(10); }
		clientPlayer.setRelativeAngle(MoveState.IDLE);
		Thread.sleep(100);
	}

	/**
	 * Move the client player to the given vector.
	 *
	 * @param newVector The vector to which to move.
	 *
	 * @throws InterruptedException If there's an exception.
	 */
	private void movePlayerTowardVector(Vector2D newVector) throws InterruptedException
	{
		clientPlayer.setRelativeAngle(clientPlayer.getVector2D().getAngleToVector(newVector));
		while (clientPlayer.getVector2D().getDistanceToVector(newVector) > 10.0) { Thread.sleep(10); }
		clientPlayer.setRelativeAngle(MoveState.IDLE);
		Thread.sleep(100);
	}

	/**
	 * Get a random vector in the world's range.
	 *
	 * @return A random Vector2D.
	 */
	private Vector2D getRandomVector()
	{
		double maxCoords = world.SIZE * Location.SIZE;
		double randomX = Math.random() * maxCoords;
		double randomZ = Math.random() * maxCoords;
		return new Vector2D(randomX, randomZ);
	}

	/**
	 * Test the actor visiblity after player and actor movement.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testActorVisibilty() throws Exception
	{
		serverActor = new Actor("TestActor");
		serverActor.setVector2D(75, 75);
		int stepsNeeded = (int) (serverActor.getVector2D().getDistanceToVector(new Vector2D(20, 20)) / Actor.STEP_LENGTH) - 1;
		serverActor.addMoveState(new MoveState(MoveState.BACK_RIGHT, 0, 0));
		serverActor.addMoveState(new MoveState(MoveState.IDLE, stepsNeeded, 0));
		serverActor.addMoveState(new MoveState(MoveState.FRONT_LEFT, stepsNeeded, 0));
		serverActor.addMoveState(new MoveState(MoveState.IDLE, stepsNeeded, 0));
		world.addObject(serverActor);

		while (serverActor.getStepsTaken() < stepsNeeded) { Thread.sleep(10); }

		assertEquals(1, worldClient.getVisibleObjects().size());
		Thread.sleep(100);
		assertEquals(serverActor.getVector2D(), worldClient.getVisibleObjects().get(serverActor.getId()).getVector2D());
		while (!serverActor.getVector2D().equals(new Vector2D(75, 75))) { Thread.sleep(1); }
		assertEquals(0, worldClient.getVisibleObjects().size());

		movePlayerTowardVector(new Vector2D(60, 60));
		assertEquals(1, worldClient.getVisibleObjects().size());
		Thread.sleep(100);
		assertEquals(serverActor.getVector2D(), worldClient.getVisibleObjects().get(serverActor.getId()).getVector2D());

		movePlayerToOrigin();
		assertEquals(0, worldClient.getVisibleObjects().size());
	}

	/**
	 * Test the state-forcing funcionalty.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testForcedState() throws Exception
	{
		clientPlayer.setRelativeAngle(MoveState.FRONT);
		while (clientPlayer.getStepsTaken() < 5) { Thread.sleep(10); }
		clientPlayer.setRelativeAngle(MoveState.BACK_LEFT);
		while (clientPlayer.getStepsTaken() < 7) { Thread.sleep(10); }
		Thread.sleep(100);
		assertEquals(serverPlayer.getVector2D(), clientPlayer.getVector2D());
	}
}
