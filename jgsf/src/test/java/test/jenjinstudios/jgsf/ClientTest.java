package test.jenjinstudios.jgsf;

import com.jenjinstudios.jgcf.Client;
import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.jgsf.SQLHandler;
import com.jenjinstudios.jgsf.Server;
import org.junit.*;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * The client class for the Chat program tutorial.
 *
 * @author Caleb Brinkman
 */
public class ClientTest
{
	/** The chat server used for testing. */
	private static Server<ClientHandler> server;
	/** This client should login successfully. */
	private static Client goodClient01;
	/** This client should login successfully. */
	private static Client goodClient02;
	/** This client will have the same credentials as goodClient01. */
	private static Client sameClient;
	/** This client should fail to login. */
	private static Client badClient;

	/**
	 * Set up and run a server for this test.
	 *
	 * @throws java.sql.SQLException If there is an error connecting to the MySql database.
	 */
	@BeforeClass
	public static void construct() throws SQLException
	{
		/* The SQLHandler used for testing. */
		SQLHandler sqlHandler = new SQLHandler("localhost", "jenjinst_chatservertest", "jenjinst_cstest",
				"chat_test");
		server = new Server<>(50, 51019, ClientHandler.class);
		server.setSQLHandler(sqlHandler);
		server.blockingStart();
	}

	/**
	 * Shut the server down when all is said and done.
	 *
	 * @throws java.io.IOException If there is an error shutting down the server.
	 */
	@AfterClass
	public static void destroy() throws IOException
	{
		server.shutdown();
	}

	/** Set up clients for each test. */
	@Before
	public void setUpClients()
	{
		goodClient01 = new Client("localhost", 51019, "TestAccount01", "testPassword");
		goodClient01.blockingStart();

		goodClient02 = new Client("127.0.0.1", 51019, "TestAccount02", "testPassword");
		goodClient02.blockingStart();

		sameClient = new Client("127.0.0.1", 51019, "TestAccount01", "testPassword");
		sameClient.blockingStart();

		badClient = new Client("127.0.0.1", 51019, "TestAccount02", "This is an incorrect password.  Teehee.");
		badClient.blockingStart();

		Assert.assertTrue(goodClient01.isRunning());

		Assert.assertTrue(goodClient02.isRunning());

		Assert.assertTrue(sameClient.isRunning());

		Assert.assertTrue(badClient.isRunning());
	}

	/** Destroy clients after each test. */
	@After
	public void destroyClients()
	{
		goodClient01.shutdown();
		goodClient02.shutdown();
		sameClient.shutdown();
		badClient.shutdown();
	}

	/** Test the login and logout functionality. */
	@Test
	public void testLoginLogout()
	{
		goodClient01.sendLoginRequest();
		assertTrue(goodClient01.isLoggedIn());

		goodClient01.sendLogoutRequest();
		assertFalse(goodClient01.isLoggedIn());
	}

	/** Test the submission of an incorrect password. */
	@Test
	public void testIncorrectPassword()
	{
		badClient.sendLoginRequest();
		assertFalse(badClient.isLoggedIn());
	}

	/** Test the login time. */
	@Test
	public void testGetLoggedInTime()
	{
		goodClient01.sendLoginRequest();
		ClientHandler handler = server.getClientHandlerByUsername("TestAccount01");
		assertEquals(handler.getLoggedInTime(), goodClient01.getLoggedInTime());
		goodClient01.sendLogoutRequest();
	}

	/** Test the login functionality for when clients are already logged in. */
	@Test
	public void testAlreadyLoggedIn()
	{
		goodClient01.sendLoginRequest();
		assertTrue(goodClient01.isLoggedIn());

		sameClient.sendLoginRequest();
		assertFalse(sameClient.isLoggedIn());

		goodClient01.sendLogoutRequest();
		assertFalse(sameClient.isLoggedIn());
	}

	/**
	 * Test the emergency logout functionality.
	 *
	 * @throws InterruptedException If the sleep is interrupted.
	 */
	@Test
	public void testEmergencyLogout() throws InterruptedException
	{
		// This client logs in and shuts down before sending a proper logout request.
		// The server should auto logout the client
		goodClient01.sendLoginRequest();
		assertTrue(goodClient01.isLoggedIn());
		goodClient01.shutdown();
		// Have to sleep.  It's HIGHLY unlikely that a client will try logging in less than the minimum sleep resolution
		// after a broken connection.
		Thread.sleep(100);
		// sameClient logs in, and should be able to successfully since the server auto logged out the failed connection.
		sameClient.sendLoginRequest();
		assertTrue(sameClient.isLoggedIn());

		sameClient.sendLogoutRequest();
		assertFalse(sameClient.isLoggedIn());
		// It is important to note that if the server dies the entire database will be corrupted.  Recommend using an
		// hourly auto-backup in case of server failure.
	}
}
