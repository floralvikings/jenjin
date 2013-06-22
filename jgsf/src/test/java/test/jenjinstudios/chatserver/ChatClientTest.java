package test.jenjinstudios.chatserver;

import com.jenjinstudios.chatserver.ChatClientHandler;
import com.jenjinstudios.jgcf.Client;
import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.jgsf.SQLHandler;
import com.jenjinstudios.jgsf.Server;
import com.jenjinstudios.message.ChatBroadcast;
import com.jenjinstudios.message.ChatMessage;
import org.junit.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Test the chat functionality of the client.
 *
 * @author Caleb Brinkman
 */
public class ChatClientTest
{
	/** The chat server used for testing. */
	private static Server<ChatClientHandler> chatServer;
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
	 * @throws SQLException If there is an error connecting to the MySql database.
	 */
	@BeforeClass
	public static void construct() throws SQLException
	{
		/* The SQLHandler used for testing. */
		SQLHandler sqlHandler = new SQLHandler("localhost", "jenjinst_chatservertest", "jenjinst_cstest",
				"chat_test");
		chatServer = new Server<>(50, 51019, ChatClientHandler.class);
		chatServer.setSQLHandler(sqlHandler);
		chatServer.blockingStart();
	}

	/**
	 * Shut the server down when all is said and done.
	 *
	 * @throws IOException If there is an error shutting down the server.
	 */
	@AfterClass
	public static void destroy() throws IOException
	{
		chatServer.shutdown();
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
		ClientHandler handler = chatServer.getClientHandlerByUsername("TestAccount01");
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
	 * Test the chat functioality.
	 *
	 * @throws InterruptedException If there is an error during thread sleep.
	 */
	@Test
	public void testSendChatMessage() throws InterruptedException
	{
		String message = "Hello world.";

		goodClient01.sendLoginRequest();
		assertTrue(goodClient01.isLoggedIn());

		goodClient02.sendLoginRequest();
		assertTrue(goodClient02.isLoggedIn());

		goodClient01.sendChatMessage(new ChatMessage(message));

		// A few lines to block until the second client receives the message.  5 second timeout.
		long startTime = System.currentTimeMillis();
		LinkedList<ChatBroadcast> receivedMessages = goodClient02.getChatMessages();
		while (receivedMessages.isEmpty() && (System.currentTimeMillis() - startTime) < 5000)
			receivedMessages = goodClient02.getChatMessages();

		String receivedMessage = "Not the correct message.";

		if (!receivedMessages.isEmpty())
		{
			receivedMessage = receivedMessages.pop().toString();
		}

		assertTrue(receivedMessage.contains(message));

		goodClient01.sendLogoutRequest();
		goodClient02.sendLogoutRequest();
	}

	/** Test the emergency logout funcionality. */
	@Test
	public void testEmergencyLogout()
	{
		// This client logs in and shuts down before sending a proper logout request.
		// The server should auto logout the client
		goodClient01.sendLoginRequest();
		goodClient01.shutdown();

		// sameClient logs in, and should be able to successfully since the server auto logged out the failed connection.
		sameClient.sendLoginRequest();
		assertTrue(sameClient.isLoggedIn());

		sameClient.sendLogoutRequest();
		assertFalse(sameClient.isLoggedIn());
		// It is important to note that if the server dies the entire database will be corrupted.  Recommend using an
		// hourly auto-backup in case of server failure.
	}
}
