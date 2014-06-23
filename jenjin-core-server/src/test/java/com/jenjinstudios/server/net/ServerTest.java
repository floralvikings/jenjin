package com.jenjinstudios.server.net;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.sql.SQLHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * The client class for the Chat program tutorial.
 * @author Caleb Brinkman
 */
public class ServerTest
{
	private static final Logger LOGGER = Logger.getLogger(ServerTest.class.getName());
	/** The chat server used for testing. */
	private static AuthServer<ClientHandler> server;
	/** This client should login successfully. */
	private static AuthClient goodClient01;
	/** This client will have the same credentials as goodClient01. */
	private static AuthClient sameClient;
	/** The time this test suite started. */
	private static long startTime;
	/** The String used in connection protocol. */
	public static final String CONNECTION_STRING_PROTOCOL = "jdbc:mysql:thin://";
	/** The MessageRegistry used for this test. */
	private static MessageRegistry mr;

	/**
	 * Set up and run a server for this test.
	 * @throws Exception If there is an error connecting to the MySql database.
	 */
	@BeforeClass
	public static void construct() throws Exception {
		mr = new MessageRegistry();
		// TODO Move these assertions.
		SQLHandler sqlHandler = getSqlHandler();

		server = new AuthServer<>(mr, 50, 51019, ClientHandler.class, sqlHandler);
		assertTrue(server.blockingStart());

		startTime = System.currentTimeMillis();

		assertTrue(server.isInitialized());
	}

	private static SQLHandler getSqlHandler() throws SQLException {
		String dbAddress = "localhost";
		String dbName = "jenjin_test";
		String dbUsername = "jenjin_user";
		String dbPassword = "jenjin_password";
		String dbUrl = CONNECTION_STRING_PROTOCOL + dbAddress + "/" + dbName;
		try
		{
			Class.forName("org.drizzle.jdbc.DrizzleDriver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to register Drizzle driver; is the Drizzle dependency present?");
		}
		Connection dbConnection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		return new SQLHandler(dbConnection);
	}

	/**
	 * Shut the server down when all is said and done.
	 * @throws java.io.IOException If there is an error shutting down the server.
	 * @throws InterruptedException If there is interrupt.
	 */
	@AfterClass
	public static void destroy() throws IOException, InterruptedException {
		if (goodClient01 != null)
		{
			if (goodClient01.isLoggedIn())
				assertTrue(goodClient01.sendBlockingLogoutRequest());
			goodClient01.shutdown();
		}

		while ((System.currentTimeMillis() - startTime) < 1500)
			Thread.sleep(1);
		server.shutdown();
	}

	/**
	 * Test the login and logout functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLoginLogout() throws Exception {
		Socket sock = new Socket("localhost", 51019);
		MessageInputStream in = new MessageInputStream(mr, sock.getInputStream());
		MessageOutputStream out = new MessageOutputStream(mr, sock.getOutputStream());
		goodClient01 = new AuthClient(in, out, mr, "TestAccount1", "testPassword");
		goodClient01.blockingStart();

		assertTrue(goodClient01.sendBlockingLoginRequest());
		assertTrue(goodClient01.isLoggedIn());

		assertEquals(1, server.getNumClients());

		assertTrue(goodClient01.sendBlockingLogoutRequest());
		assertFalse(goodClient01.isLoggedIn());

		goodClient01.shutdown();
	}

	/**
	 * Test the submission of an incorrect password.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testIncorrectPassword() throws Exception {
		/* This client should fail to login. */
		Socket sock = new Socket("127.0.0.1", 51019);
		MessageInputStream in = new MessageInputStream(mr, sock.getInputStream());
		MessageOutputStream out = new MessageOutputStream(mr, sock.getOutputStream());
		AuthClient badClient = new AuthClient(in, out, mr, "TestAccount2", "This is an incorrect password.");
		badClient.blockingStart();

		assertFalse(badClient.sendBlockingLoginRequest());
		assertFalse(badClient.isLoggedIn());

		badClient.shutdown();
	}

	/**
	 * Test the login time.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testGetLoggedInTime() throws Exception {
		Socket sock = new Socket("localhost", 51019);
		MessageInputStream in = new MessageInputStream(mr, sock.getInputStream());
		MessageOutputStream out = new MessageOutputStream(mr, sock.getOutputStream());
		goodClient01 = new AuthClient(in, out, mr, "TestAccount1", "testPassword");
		goodClient01.blockingStart();

		assertTrue(goodClient01.isRunning());

		assertTrue(goodClient01.sendBlockingLoginRequest());
		ClientHandler handler = server.getClientHandlerByUsername("TestAccount1");
		assertEquals(handler.getLoggedInTime(), goodClient01.getLoggedInTime());
		assertTrue(goodClient01.sendBlockingLogoutRequest());

		goodClient01.shutdown();
	}

	/**
	 * Test the login functionality for when clients are already logged in.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testAlreadyLoggedIn() throws Exception {
		Socket sock = new Socket("localhost", 51019);
		MessageInputStream in = new MessageInputStream(mr, sock.getInputStream());
		MessageOutputStream out = new MessageOutputStream(mr, sock.getOutputStream());
		goodClient01 = new AuthClient(in, out, mr, "TestAccount1", "testPassword");
		assertTrue(goodClient01.blockingStart());

		sock = new Socket("127.0.0.1", 51019);
		MessageInputStream in2 = new MessageInputStream(mr, sock.getInputStream());
		MessageOutputStream out2 = new MessageOutputStream(mr, sock.getOutputStream());
		sameClient = new AuthClient(in2, out2, mr, "TestAccount1", "testPassword");
		sameClient.blockingStart();

		assertTrue(goodClient01.sendBlockingLoginRequest());
		assertTrue(goodClient01.isLoggedIn());

		assertFalse(sameClient.sendBlockingLoginRequest());
		assertFalse(sameClient.isLoggedIn());

		assertTrue(goodClient01.sendBlockingLogoutRequest());
		assertFalse(sameClient.isLoggedIn());

		goodClient01.shutdown();
		sameClient.shutdown();
	}

	/**
	 * Test the emergency logout functionality.
	 * @throws InterruptedException If the sleep is interrupted.
	 */
	@Test(timeOut = 10000)
	public void testEmergencyLogout() throws Exception {
		Socket sock = new Socket("localhost", 51019);
		MessageInputStream in = new MessageInputStream(mr, sock.getInputStream());
		MessageOutputStream out = new MessageOutputStream(mr, sock.getOutputStream());
		goodClient01 = new AuthClient(in, out, mr, "TestAccount1", "testPassword");
		LOGGER.log(Level.INFO, "Starting goodClient01");
		goodClient01.blockingStart();

		sock = new Socket("127.0.0.1", 51019);
		MessageInputStream in2 = new MessageInputStream(mr, sock.getInputStream());
		MessageOutputStream out2 = new MessageOutputStream(mr, sock.getOutputStream());
		sameClient = new AuthClient(in2, out2, mr, "TestAccount1", "testPassword");
		LOGGER.log(Level.INFO, "Starting sameClient");
		sameClient.blockingStart();

		// This client logs in and shuts down before sending a proper logout request.
		// The server should auto logout the client
		LOGGER.log(Level.INFO, "goodClient01 Logging in");
		assertTrue(goodClient01.sendBlockingLoginRequest());
		assertTrue(goodClient01.isLoggedIn());
		LOGGER.log(Level.INFO, "goodClient01 Logged in successfully, forcing shutdown");
		goodClient01.shutdown();
		// Have to sleep.  It's HIGHLY unlikely that a client will try logging in less than the minimum sleep resolution
		// after a broken connection.
		Thread.sleep(server.PERIOD);
		// sameClient logs in, and should be able to successfully since the server auto logged out the failed connection.
		LOGGER.log(Level.INFO, "sameClient Logging in");
		assertTrue(sameClient.sendBlockingLoginRequest());
		assertTrue(sameClient.isLoggedIn());
		LOGGER.log(Level.INFO, "sameClient Logged in successfully");
		assertTrue(sameClient.sendBlockingLogoutRequest());
		assertFalse(sameClient.isLoggedIn());
		// It is important to note that if the server dies the entire database will be corrupted.  Recommend using an
		// hourly auto-backup in case of server failure.

		goodClient01.shutdown();
		sameClient.shutdown();
	}
}
