package test.jenjinstudios.chatserver;

import com.jenjinstudios.chatserver.ChatServer;
import com.jenjinstudios.jgsf.SQLHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test various functionalities of the ChatServer.
 *
 * @author Caleb Brinkman
 */
public class ChatServerTest
{
	/** The SQLHandler to be used for testing. */
	private static SQLHandler sqlHandler;
	/** The ChatServer to be used for testing. */
	private static ChatServer server;


	/**
	 * Set up and start the chat server.
	 *
	 * @throws SQLException If there is an error connecting to the MySQL database.
	 */
	@BeforeClass
	public static void construct() throws SQLException
	{
		server = new ChatServer();
		sqlHandler = new SQLHandler("localhost", "jenjinst_chatservertest", "jenjinst_cstest", "chat_test");
		server.setSQLHandler(sqlHandler);
		server.blockingStart();
	}

	/** Test the SQLHandler's connection. */
	@Test
	public void testSQLHandlerConnected()
	{
		assertTrue(sqlHandler.isConnected());
	}

	/** Test the server's initialization status. */
	@Test
	public void testIsInitialized()
	{
		assertTrue(server.isInitialized());
	}

	/** Verify that the server has connected to the database. */
	@Test
	public void testIsConnectedToDB()
	{
		assertTrue(server.isConnectedToDB());
	}

	/** Test the get and set functionality for max clients. */
	@Test
	public void testMaxClients()
	{
		server.setMaxClients(1000);
		assertEquals(1000, server.getMaxClients());
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
}
