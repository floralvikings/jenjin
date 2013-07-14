package test.jenjinstudios.chatserver;

import com.jenjinstudios.chatserver.ChatClientHandler;
import com.jenjinstudios.jgsf.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test various Chat functions of the Server.
 *
 * @author Caleb Brinkman
 */
public class ChatServerTest
{
	/** The Server to be used for testing. */
	private static Server<ChatClientHandler> server;

	/**
	 * Set up and start the chat server.
	 *
	 * @throws SQLException If there is an error connecting to the MySQL database.
	 */
	@BeforeClass
	public static void construct() throws SQLException
	{
		server = new Server<>(50, 51019, ChatClientHandler.class);
		server.blockingStart();
	}

	/**
	 * Destroy the server after testing.
	 *
	 * @throws IOException If there is an error in closing streams or sockets.
	 */
	@AfterClass
	public static void destroy() throws IOException
	{
		server.shutdown();
	}

	/** Test the server's initialization status. */
	@Test
	public void testIsInitialized()
	{
		assertTrue(server.isInitialized());
	}

	/** Test the get and set functionality for max clients. */
	@Test
	public void testMaxClients()
	{
		server.setMaxClients(1000);
		assertEquals(1000, server.getMaxClients());
	}
}
