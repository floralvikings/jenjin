package com.jenjinstudios.server.net;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.sql.SQLHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
	private AuthServer<ClientHandler> server;
	/** The String used in connection protocol. */
	public static final String CONNECTION_STRING_PROTOCOL = "jdbc:mysql:thin://";
	/** The MessageRegistry used for this test. */
	private static MessageRegistry mr;

	/**
	 * Set up and run a server for this test.
	 * @throws Exception If there is an error connecting to the MySql database.
	 */
	@BeforeClass
	public void construct() throws Exception {
		mr = new MessageRegistry();
		SQLHandler sqlHandler = getSqlHandler();
		server = new AuthServer<>(mr, 50, 51019, ClientHandler.class, sqlHandler);
		server.blockingStart();
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
	public void destroy() throws IOException, InterruptedException {
		server.shutdown();
	}

	@Test
	public void testBlockingStart() throws Exception {
		SQLHandler sqlHandler = getSqlHandler();

		Server server = new AuthServer<>(mr, 50, 51020, ClientHandler.class, sqlHandler);
		assertTrue(server.blockingStart());
		server.shutdown();
	}

	@Test
	public void testIsInitialized() throws Exception {
		SQLHandler sqlHandler = getSqlHandler();

		Server server = new AuthServer<>(mr, 50, 51020, ClientHandler.class, sqlHandler);
		server.blockingStart();
		assertTrue(server.isInitialized());
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
		prepareForLoginRequest(in, out);

		Message loginRequest = mr.createMessage("LoginRequest");
		loginRequest.setArgument("username", "TestAccount1");
		loginRequest.setArgument("password", "testPassword");
		out.writeMessage(loginRequest);

		Message loginResponse = in.readMessage();
		assertTrue((boolean) loginResponse.getArgument("success"));

		Message logoutRequest = mr.createMessage("LogoutRequest");
		out.writeMessage(logoutRequest);

		Message logoutResponse = in.readMessage();
		assertTrue((boolean) logoutResponse.getArgument("success"));
		sock.close();
	}

	private void prepareForLoginRequest(MessageInputStream in, MessageOutputStream out) throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(512);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		byte[] clientKey = keyPair.getPublic().getEncoded();

		Message publicKeyMessage = mr.createMessage("PublicKeyMessage");
		publicKeyMessage.setArgument("key", clientKey);
		out.writeMessage(publicKeyMessage);

		in.readMessage();
		Message aes = in.readMessage();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
		byte[] decKey = cipher.doFinal((byte[]) aes.getArgument("key"));
		in.setAESKey(decKey);
		out.setAesKey(decKey);
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
		prepareForLoginRequest(in, out);

		Message loginRequest = mr.createMessage("LoginRequest");
		loginRequest.setArgument("username", "TestAccount2");
		loginRequest.setArgument("password", "This is an incorrect password.");
		out.writeMessage(loginRequest);

		Message loginResponse = in.readMessage();
		assertFalse((boolean) loginResponse.getArgument("success"));
		sock.close();
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
		prepareForLoginRequest(in, out);

		Message loginRequest = mr.createMessage("LoginRequest");
		loginRequest.setArgument("username", "TestAccount1");
		loginRequest.setArgument("password", "testPassword");
		out.writeMessage(loginRequest);

		Message loginResponse = in.readMessage();
		long loggedIn = (long) loginResponse.getArgument("loginTime");

		ClientHandler handler = server.getClientHandlerByUsername("TestAccount1");
		assertEquals(handler.getLoggedInTime(), loggedIn);

		Message logoutRequest = mr.createMessage("LogoutRequest");
		out.writeMessage(logoutRequest);
		sock.close();
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
		prepareForLoginRequest(in, out);

		Message loginRequest = mr.createMessage("LoginRequest");
		loginRequest.setArgument("username", "TestAccount1");
		loginRequest.setArgument("password", "testPassword");
		out.writeMessage(loginRequest);

		Message loginResponse = in.readMessage();
		assertTrue((boolean) loginResponse.getArgument("success"));

		Socket sock2 = new Socket("127.0.0.1", 51019);
		MessageInputStream in2 = new MessageInputStream(mr, sock2.getInputStream());
		MessageOutputStream out2 = new MessageOutputStream(mr, sock2.getOutputStream());
		prepareForLoginRequest(in2, out2);
		out2.writeMessage(loginRequest);

		loginResponse = in2.readMessage();
		assertFalse((boolean) loginResponse.getArgument("success"));
		sock.close();
		sock2.close();
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
		prepareForLoginRequest(in, out);

		Message loginRequest = mr.createMessage("LoginRequest");
		loginRequest.setArgument("username", "TestAccount1");
		loginRequest.setArgument("password", "testPassword");
		out.writeMessage(loginRequest);

		Message loginResponse = in.readMessage();
		assertTrue((boolean) loginResponse.getArgument("success"));
		sock.close();

		Thread.sleep(server.PERIOD * 2);

		Socket sock2 = new Socket("127.0.0.1", 51019);
		MessageInputStream in2 = new MessageInputStream(mr, sock2.getInputStream());
		MessageOutputStream out2 = new MessageOutputStream(mr, sock2.getOutputStream());
		prepareForLoginRequest(in2, out2);

		out2.writeMessage(loginRequest);
		loginResponse = in2.readMessage();
		assertTrue((boolean) loginResponse.getArgument("success"));

		Message logoutRequest = mr.createMessage("LogoutRequest");
		out2.writeMessage(logoutRequest);
		Message logoutResponse = in2.readMessage();
		assertTrue((boolean) logoutResponse.getArgument("success"));
		sock2.close();
	}
}
