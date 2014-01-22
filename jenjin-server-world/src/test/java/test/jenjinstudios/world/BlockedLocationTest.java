package test.jenjinstudios.world;

import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.world.*;
import com.jenjinstudios.world.io.WorldFileReader;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.sql.WorldSQLHandler;
import org.junit.*;

import java.io.InputStream;

/**
 * Test the file-loading and blocked location functionality.
 * @author Caleb Brinkman
 */
public class BlockedLocationTest
{
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
	 * Set up the test.
	 * @throws Exception If there's an Exception.
	 */
	@Before
	public void setUp() throws Exception {


		initWorld();
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
	}

	/**
	 * Test attempting to walk into a "blocked" location.
	 * @throws Exception If there's an Exception.
	 */
	@Test
	public void TestAttemptBlockedLocation() throws Exception
	{
		WorldTestUtils.moveClientPlayerTowardVector(new Vector2D(0, Location.SIZE * 3), clientPlayer, serverPlayer);
		Assert.assertEquals(serverPlayer.getVector2D(), clientPlayer.getVector2D());
		Assert.assertEquals(new Vector2D(0.0, 9.8), serverPlayer.getVector2D());
	}

	/**
	 * Initialize the world.
	 * @throws Exception If there's an Exception.
	 */
	private void initWorld() throws Exception {
		InputStream resourceAsStream = getClass().getResourceAsStream("/WorldTest01.xml");
		WorldFileReader testReader = new WorldFileReader(resourceAsStream);
		world = testReader.read();
		Location testLocation = world.getLocationForCoordinates(0, new Vector2D(0, Location.SIZE));
		Assert.assertEquals(false, testLocation.getLocationProperties().isWalkable);
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
		worldServer = new WorldServer(world, worldSQLHandler);
		worldServer.blockingStart();
	}
}
