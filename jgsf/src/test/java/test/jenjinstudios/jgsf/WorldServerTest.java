package test.jenjinstudios.jgsf;

import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.jgsf.WorldClientHandler;
import com.jenjinstudios.jgsf.WorldServer;
import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.net.WorldClient;
import com.jenjinstudios.sql.WorldSQLHandler;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.ClientObject;
import com.jenjinstudios.world.ClientPlayer;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.state.MoveState;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jenjinstudios.world.state.MoveState.IDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test the world server.
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

	/** Construct the test. */
	@BeforeClass
	public static void construct() {
		MessageRegistry.registerXmlMessages(true);
	}

	/**
	 * Set up the client and server.
	 * @throws Exception If there's an exception.
	 */
	@Before
	public void setUp() throws Exception {
		initWorldServer();
		initWorldClient();
	}

	/**
	 * Initialize the world and world server.
	 * @throws Exception If there's an exception.
	 */
	private void initWorldServer() throws Exception {
		worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user", "jenjin_password");
		worldServer = new WorldServer(worldSQLHandler);
		world = worldServer.getWorld();
		// FIXME Blocking on login request if SQL handler not set. Bad.
		worldServer.blockingStart();
	}

	/**
	 * Initialize and log the client in.
	 * @throws Exception If there's an exception.
	 */
	private void initWorldClient() throws Exception {
		worldClient = new WorldClient("localhost", WorldServer.DEFAULT_PORT, "TestAccount01", "testPassword");
		worldClient.blockingStart();
		worldClient.sendBlockingLoginRequest();

		worldClientHandler = worldServer.getClientHandlerByUsername(worldClient.getUsername());
		clientPlayer = worldClient.getPlayer();
		serverPlayer = worldClientHandler.getPlayer();
	}

	/**
	 * Tear down the client and server.
	 * @throws Exception If there's an exception.
	 */
	@After
	public void tearDown() throws Exception {
		serverPlayer.setVector2D(new Vector2D(0, 0));
		worldClient.sendBlockingLogoutRequest();
		worldClient.shutdown();

		worldServer.shutdown();
	}

	/**
	 * Move the client player to the given vector.
	 * @param newVector The vector to which to move.
	 * @throws InterruptedException If there's an exception.
	 */
	private void movePlayerTowardVector(Vector2D newVector) throws InterruptedException {
		clientPlayer.setNewRelativeAngle(clientPlayer.getVector2D().getAngleToVector(newVector));
		while (clientPlayer.getVector2D().getDistanceToVector(newVector) > Actor.STEP_LENGTH) { Thread.sleep(10); }
		clientPlayer.setNewRelativeAngle(IDLE);
		Thread.sleep(100);
	}

	/**
	 * Move the player to the origin.
	 * @throws InterruptedException If there's an exception.
	 */
	private void movePlayerToOrigin() throws InterruptedException {
		clientPlayer.setNewRelativeAngle(clientPlayer.getVector2D().getAngleToVector(Vector2D.ORIGIN));
		while (clientPlayer.getVector2D().getDistanceToVector(Vector2D.ORIGIN) > Actor.STEP_LENGTH)
		{
			Thread.sleep(10);
		}
		clientPlayer.setNewRelativeAngle(IDLE);
		Thread.sleep(100);
	}

	/**
	 * Test the actor visiblity after player and actor movement.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testActorVisibilty() throws Exception {
		Vector2D actorOrigin = new Vector2D(21, 21);
		Vector2D targetVector = new Vector2D(10, 10);
		double targetAngle = actorOrigin.getAngleToVector(targetVector);
		double backwardAngle = Math.PI + targetAngle;
		serverActor = new Actor("TestActor");
		serverActor.setVector2D(actorOrigin);
		int stepsNeeded = (int) (serverActor.getVector2D().getDistanceToVector(targetVector) / Actor.STEP_LENGTH);

		serverActor.addMoveState(new MoveState(targetAngle, 0, 0));
		serverActor.addMoveState(new MoveState(IDLE, stepsNeeded, 0));
		serverActor.addMoveState(new MoveState(backwardAngle, stepsNeeded, 0));
		serverActor.addMoveState(new MoveState(IDLE, stepsNeeded, 0));
		world.addObject(serverActor);

		while (serverActor.getStepsTaken() < stepsNeeded)
		{
			// System.out.println(serverActor.getVector2D());
			Thread.sleep(10);
		}

		ClientObject clientActor = worldClient.getVisibleObjects().get(serverActor.getId());
		assertEquals(1, worldClient.getVisibleObjects().size());
		Thread.sleep(100);
		assertEquals(serverActor.getVector2D(), clientActor.getVector2D());
		while (!serverActor.getVector2D().equals(actorOrigin)) { Thread.sleep(10); }
		assertEquals(0, worldClient.getVisibleObjects().size());


		movePlayerTowardVector(new Vector2D(11, 11));
		assertEquals(1, worldClient.getVisibleObjects().size());
		Thread.sleep(100);
		clientActor = worldClient.getVisibleObjects().get(serverActor.getId());
		assertEquals(serverActor.getVector2D(), clientActor.getVector2D());

		movePlayerToOrigin();
		assertEquals(0, worldClient.getVisibleObjects().size());
	}

	/**
	 * Test the state-forcing funcionalty.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testForcedState() throws Exception {
		clientPlayer.setNewRelativeAngle(MoveState.FRONT);
		while (clientPlayer.getStepsTaken() < 5) { Thread.sleep(10); }
		clientPlayer.setNewAbsoluteAngle(Math.PI);
		while (!clientPlayer.isForcedState()) { Thread.sleep(10); }
		Thread.sleep(200);
		assertFalse(clientPlayer.isForcedState());
		assertEquals(serverPlayer.getVector2D(), clientPlayer.getVector2D());
	}
}
