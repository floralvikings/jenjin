package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.sql.UserTable;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Test the Authenticator class.
 * @author Caleb Brinkman
 */
public class AuthenticatorTest
{
	private static int connectionNumber = 0;
	private static Connection connection;

	/**
	 * Create a unique connection with some dummy data that we can test on.
	 *
	 * @return The dummy connection.
	 *
	 * @throws Exception If something goes wrong creating the connection.
	 */
	private static Connection createTestConnection() throws Exception {
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
			  ")");
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
		return testConnection;
	}

	@BeforeClass
	public static void createConnection() throws Exception {
		connection = createTestConnection();
	}

	@AfterClass
	public static void closeConnection() throws Exception {
		connection.close();
	}

	@Test
	public void testLogInUser() throws Exception {
		Authenticator<BasicUser> connector = new Authenticator<>(new UserTable(connection));
		String username = "TestAccount2";
		String password = "testPassword";
		connector.logInUser(username, password);
		User user = connector.getUserLookup().findUser(username);
		Assert.assertTrue(user.isLoggedIn());

	}

	@Test(expectedExceptions = AuthenticationException.class)
	public void testConcurrentLogins() throws Exception {
		Authenticator<BasicUser> connector = new Authenticator<>(new UserTable(connection));
		String username = "TestAccount3";
		String password = "testPassword";
		connector.logInUser(username, password);
		// Concurrent login isn't aren't allowed.
		connector.logInUser(username, password);

	}

	@Test
	public void testLogOutUser() throws Exception {
		Authenticator<BasicUser> connector = new Authenticator<>(new UserTable(connection));
		String username = "TestAccount4";
		String password = "testPassword";
		connector.logInUser(username, password);
		connector.logOutUser(username);
		User user = connector.getUserLookup().findUser(username);
		Assert.assertFalse(user.isLoggedIn());

	}

	@Test
	public void testInvalidPassword() throws Exception {
		Authenticator<BasicUser> connector = new Authenticator<>(new UserTable(connection));
		String username = "TestAccount5";
		String password = "incorrectPassword";
		User user = connector.logInUser(username, password);
		Assert.assertNull(user, "Invalid password should result in null user.");
	}
}
