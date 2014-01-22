package test.jenjinstudios.world;

import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.world.*;
import com.jenjinstudios.world.math.Round;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.sql.WorldSQLHandler;
import org.junit.*;

/**
 * Test the world server.
 * @author Caleb Brinkman
 */
public class WorldServerTest
{
	// Server fields
	/** The world server used to test. */
	private WorldServer worldServer;
	/** The world used for testing. */
	private World world;
	/** The server-side actor representing the player. */
	private Actor serverPlayer;

	// Client fields
	/** The world client used to test. */
	private WorldClient worldClient;
	/** The client-side player used for testing. */
	private ClientPlayer clientPlayer;

	/**
	 * Construct the test.
	 * @throws Exception If there's an Exception.
	 */
	@BeforeClass
	public static void construct() throws Exception { MessageRegistry.registerXmlMessages(true); }

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
	 * Test the actor visiblity after player and actor movement.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testActorVisibilty() throws Exception {
		Vector2D serverActorStartPosition = new Vector2D(0, 51);
		Vector2D serverActorTargetPosition = new Vector2D(0, 49);
		Actor serverActor = new Actor("TestActor");
		serverActor.setVector2D(serverActorStartPosition);
		world.addObject(serverActor);

		WorldTestUtils.moveServerActorToVector(serverActor, serverActorTargetPosition);

		ClientObject clientActor = worldClient.getVisibleObjects().get(serverActor.getId());
		Assert.assertNotNull(clientActor);
		Assert.assertEquals(1, worldClient.getVisibleObjects().size());
		Thread.sleep(50);
		Assert.assertEquals(serverActor.getVector2D(), clientActor.getVector2D());

		WorldTestUtils.moveServerActorToVector(serverActor, serverActorStartPosition);
		Assert.assertEquals(0, worldClient.getVisibleObjects().size());


		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(0, 11), clientPlayer, serverPlayer);
		Assert.assertEquals(1, worldClient.getVisibleObjects().size());
		clientActor = worldClient.getVisibleObjects().get(serverActor.getId());
		Assert.assertEquals(serverActor.getVector2D(), clientActor.getVector2D());

		WorldTestUtils.moveClientPlayerTowardVector(Vector2D.ORIGIN, clientPlayer, serverPlayer);
		Assert.assertEquals(0, worldClient.getVisibleObjects().size());
	}

	/**
	 * Test the state-forcing funcionalty.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testForcedStateFromEdge() throws Exception {
		WorldTestUtils.idleClientPlayer(1, clientPlayer);
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(-1.0, 0), clientPlayer, serverPlayer);
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(1, 0), clientPlayer, serverPlayer);
		Assert.assertFalse(clientPlayer.isForcedState());
		Assert.assertEquals(serverPlayer.getVector2D(), clientPlayer.getVector2D());
	}

	/**
	 * Test the state forcing functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testForcedState() throws Exception {
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(0.5, 0.5), clientPlayer, serverPlayer);
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(-0.5, -0.5), clientPlayer, serverPlayer);
		WorldTestUtils.idleClientPlayer(5, clientPlayer);
		Assert.assertEquals(clientPlayer.getVector2D(), serverPlayer.getVector2D());
	}

	/**
	 * Test basic movement.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testMovement() throws Exception {
		Vector2D targetVector = new Vector2D(3.956, 3.7468);
		WorldTestUtils.moveClientPlayerTowardVector(targetVector, clientPlayer, serverPlayer);
	}

	/**
	 * Test repeatedly forcing client.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testRepeatedForcedState() throws Exception {
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(.5, .5), clientPlayer, serverPlayer);
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(-1, -1), clientPlayer, serverPlayer);
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(.5, .5), clientPlayer, serverPlayer);
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(-1, -1), clientPlayer, serverPlayer);
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(.5, .5), clientPlayer, serverPlayer);
	}

	/**
	 * Test movement to various random vectors.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testRandomMovement() throws Exception {
		WorldTestUtils.idleClientPlayer(1, clientPlayer);
		int maxCoord = 5;
		for (int i = 0; i < 10; i++)
		{
			double randomX = Round.round(Math.random() * maxCoord, 4);
			double randomY = Round.round(Math.random() * maxCoord, 4);
			Vector2D random = new Vector2D(randomX, randomY);
			WorldTestUtils.moveClientPlayerTowardVector(random, clientPlayer, serverPlayer);
			double distance = clientPlayer.getVector2D().getDistanceToVector(serverPlayer.getVector2D());
			Assert.assertEquals("Movement number " + i + " to " + random, 0, distance, .001);
		}
	}

	/**
	 * Initialize and log the client in.
	 * @throws Exception If there's an exception.
	 */
	private void initWorldClient() throws Exception {
		worldClient = new WorldClient("localhost", WorldServer.DEFAULT_PORT, "TestAccount01", "testPassword");
		worldClient.blockingStart();
		worldClient.sendBlockingLoginRequest();

		/* The WorldClientHandler used to test. */
		WorldClientHandler worldClientHandler = worldServer.getClientHandlerByUsername(worldClient.getUsername());
		clientPlayer = worldClient.getPlayer();
		serverPlayer = worldClientHandler.getPlayer();
	}

	/**
	 * Initialize the world and world server.
	 * @throws Exception If there's an exception.
	 */
	private void initWorldServer() throws Exception {
		/* The world SQL handler used to test. */
		WorldSQLHandler worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user", "jenjin_password");
		worldServer = new WorldServer(worldSQLHandler);
		world = worldServer.getWorld();
		worldServer.blockingStart();
	}

}
