package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.sql.UserTable;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

/**
 * Test the Authenticator class.
 * @author Caleb Brinkman
 */
public class AuthenticatorTest
{
	private final ConnectionFactory connectionFactory = new ConnectionFactory();

	/**
	 * Close all openend connections after execution.
	 *
	 * @throws SQLException If there's an exception when closing a connection.
	 */
	@AfterClass
	public void closeTestConnections() throws SQLException {
		connectionFactory.closeAll();
	}

	/**
	 * Test the login functionality.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLogInUser() throws Exception {
		Connection connection = connectionFactory.createTestConnection();
		Authenticator<BasicUser> connector = new Authenticator<>(new UserTable(connection));
		String username = "TestAccount2";
		String password = "testPassword";
		connector.logInUser(username, password);
		User user = connector.getUserLookup().findUser(username);
		Assert.assertTrue(user.isLoggedIn(), "User should be logged in.");

	}

	/**
	 * Test concurrent logins.
	 * @throws Exception If there's an exception.
	 */
	@Test(expectedExceptions = AuthenticationException.class)
	public void testConcurrentLogins() throws Exception {
		Connection connection = connectionFactory.createTestConnection();
		Authenticator<BasicUser> connector = new Authenticator<>(new UserTable(connection));
		String username = "TestAccount3";
		String password = "testPassword";
		connector.logInUser(username, password);
		// Concurrent login isn't aren't allowed.
		connector.logInUser(username, password);

	}

	/**
	 * Test logout functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLogOutUser() throws Exception {
		Connection connection = connectionFactory.createTestConnection();
		Authenticator<BasicUser> connector = new Authenticator<>(new UserTable(connection));
		String username = "TestAccount4";
		String password = "testPassword";
		BasicUser user = connector.logInUser(username, password);
		connector.logOutUser(user);
		Assert.assertFalse(user.isLoggedIn(), "User should not be logged in.");

	}

	/**
	 * Test authentication with an invalid password.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testInvalidPassword() throws Exception {
		Connection connection = connectionFactory.createTestConnection();
		Authenticator<BasicUser> connector = new Authenticator<>(new UserTable(connection));
		String username = "TestAccount5";
		String password = "incorrectPassword";
		User user = connector.logInUser(username, password);
		Assert.assertNull(user, "Invalid password should result in null user.");
	}

	private static class ConnectionFactory
	{
		private LinkedList<Connection> connections = new LinkedList<>();
		private int connectionNumber;

		private Connection createTestConnection() throws Exception {
			Class.forName("org.h2.Driver");
			String connectionUrl = "jdbc:h2:mem:jenjin_test" + connectionNumber;
			Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
			Statement statement = testConnection.createStatement();
			statement.executeUpdate("CREATE TABLE jenjin_users (" +
				  "  `username` VARCHAR(16) NOT NULL," +
				  "  `password` CHAR(64) NOT NULL," +
				  "  `salt` CHAR(48) NOT NULL," +
				  "  `loggedin` TINYINT NOT NULL DEFAULT '0'," +
				  "  PRIMARY KEY (username)" +
				  ')');
			for (int i = 1; i < 10; i++)
			{
				statement.executeUpdate(
					  "INSERT INTO jenjin_users " +
							"(`username`, `password`, `salt`, `loggedin`)" +
							" VALUES " +
							"('TestAccount" + i + "', " +
							"'650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd', " +
							"'e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3', " +
							"'0')");
			}
			connectionNumber++;
			connections.add(testConnection);
			return testConnection;
		}

		private void closeAll() throws SQLException {
			for (Connection connection : connections)
			{
				connection.close();
			}
		}
	}
}
