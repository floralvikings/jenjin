package test.jenjinstudios.world;

import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.world.*;
import com.jenjinstudios.world.io.WorldFileReader;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.sql.WorldSQLHandler;
import org.junit.*;

import java.io.File;

/**
 * Test the file-loading and blocked location functionality.
 * @author Caleb Brinkman
 */
public class BlockedLocationTest
{
	/** The world server used to test. */
	private WorldServer worldServer;
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
	 * Set up the test.
	 * @throws Exception If there's an Exception.
	 */
	@Before
	public void setUp() throws Exception {
		initWorldServer();
		initWorldClient();
	}

	/**
	 * Tear down the test.
	 * @throws Exception If there's an Exception.
	 */
	@After
	public void tearDown() throws Exception {
		serverPlayer.setVector2D(new Vector2D(0, 0));
		worldClient.sendBlockingLogoutRequest();
		worldClient.shutdown();

		worldServer.shutdown();

		if(!new File("WorldTestFile.xml").delete())
		{
			System.out.println("Unable to delete world file.");
		}
	}

	/**
	 * Test attempting to walk into a "blocked" location.
	 * @throws Exception If there's an Exception.
	 */
	@Test
	public void TestAttemptBlockedLocation() throws Exception {
		Vector2D vector1 = new Vector2D(35, 0);
		Vector2D attemptedVector2 = new Vector2D(35, 35);
		Vector2D actualVector2 = new Vector2D(35, 29.8);
		Vector2D vector3 = new Vector2D(35, 25);
		Vector2D vector4 = new Vector2D(25, 25);
		Vector2D vector5 = new Vector2D(25, 35);
		Vector2D attemptedVector6 = new Vector2D(35, 35);
		Vector2D actualVector6 = new Vector2D(29.8, 35);
		Vector2D attemptedVector7 = new Vector2D(35, 35);
		Vector2D actualVector7 = new Vector2D(29.8, 35);

		// Move to (35, 0)
		WorldTestUtils.moveClientPlayerTowardVector(vector1, clientPlayer, serverPlayer);
		Assert.assertEquals(vector1, clientPlayer.getVector2D());

		// Attempt to move to (35, 35)
		// This attempt should be forced to stop one step away from
		WorldTestUtils.moveClientPlayerTowardVector(attemptedVector2, clientPlayer, serverPlayer);
		Assert.assertEquals(actualVector2, clientPlayer.getVector2D());

		WorldTestUtils.moveClientPlayerTowardVector(vector3, clientPlayer, serverPlayer);
		Assert.assertEquals(vector3, clientPlayer.getVector2D());

		WorldTestUtils.moveClientPlayerTowardVector(vector4, clientPlayer, serverPlayer);
		Assert.assertEquals(vector4, clientPlayer.getVector2D());

		WorldTestUtils.moveClientPlayerTowardVector(vector5, clientPlayer, serverPlayer);
		Assert.assertEquals(vector5, clientPlayer.getVector2D());

		WorldTestUtils.moveClientPlayerTowardVector(vector5, clientPlayer, serverPlayer);
		Assert.assertEquals(vector5, clientPlayer.getVector2D());

		WorldTestUtils.moveClientPlayerTowardVector(attemptedVector6, clientPlayer, serverPlayer);
		Assert.assertEquals(actualVector6, clientPlayer.getVector2D());

		WorldTestUtils.moveClientPlayerTowardVector(attemptedVector7, clientPlayer, serverPlayer);
		Assert.assertEquals(actualVector7, clientPlayer.getVector2D());
	}

	/**
	 * Initialize and log the client in.
	 * @throws Exception If there's an exception.
	 */
	private void initWorldClient() throws Exception {
		worldClient = new WorldClient(new File("WorldTestFile.xml"), "localhost", WorldServer.DEFAULT_PORT, "TestAccount01", "testPassword");
		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
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
		worldServer = new WorldServer(new WorldFileReader(getClass().getResourceAsStream("/WorldFile01.xml")),
				WorldServer.DEFAULT_UPS, WorldServer.DEFAULT_PORT, WorldClientHandler.class, worldSQLHandler);
		worldServer.blockingStart();
	}
}
