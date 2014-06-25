package com.jenjinstudios.server.sql;

import com.jenjinstudios.server.net.User;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author Caleb Brinkman
 */
public class SQLConnectorTest
{
	private static int connectionNumber = 0;

	/**
	 * Create a unique connection with some dummy data that we can test on.
	 * @return The dummy connection.
	 * @throws Exception If something goes wrong creating the connection.
	 */
	public static Connection createTestConnection() throws Exception {
		Class.forName("org.h2.Driver");
		String connectionUrl = "jdbc:h2:mem:jenjin_test" + connectionNumber;
		Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
		Statement statement = testConnection.createStatement();
		statement.executeUpdate("CREATE TABLE users (" +
				"  `username` VARCHAR(16) NOT NULL," +
				"  `password` CHAR(64) NOT NULL," +
				"  `salt` CHAR(48) NOT NULL," +
				"  `loggedin` TINYINT NOT NULL DEFAULT '0'," +
				"  `xcoord` DOUBLE NOT NULL DEFAULT '0'," +
				"  `ycoord` DOUBLE NOT NULL DEFAULT '0'," +
				"  `zoneid` INT(11) NOT NULL DEFAULT '0'," +
				"  PRIMARY KEY (username)" +
				")");
		for (int i = 1; i < 100; i++)
		{
			statement.executeUpdate(
					"INSERT INTO users " +
							"(`username`, `password`, `salt`, `loggedin`, `xcoord`, `ycoord`, `zoneid`)" +
							" VALUES " +
							"('TestAccount" + i + "', " +
							"'650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd', " +
							"'e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3', " +
							"'0', '0', '0', '0')");
		}
		connectionNumber++;
		return testConnection;
	}

	@Test
	public void testLookUpUser() throws Exception {
		Connection connection = createTestConnection();
		SQLConnector connector = new SQLConnector(connection);
		User testAccount1 = connector.lookUpUser("TestAccount1");
		Assert.assertEquals(testAccount1.getUsername(), "TestAccount1");
		connection.close();
	}

	@Test(expectedExceptions = LoginException.class)
	public void testLookUpFakeUser() throws Exception {
		Connection connection = createTestConnection();
		SQLConnector connector = new SQLConnector(connection);
		connector.lookUpUser("This User Doesn't Exist.");
	}

	@Test
	public void testLogInUser() throws Exception {
		SQLConnector connector = new SQLConnector(createTestConnection());
		String username = "TestAccount1";
		String password = "testPassword";
		connector.logInUser(username, password);
		User user = connector.lookUpUser(username);
		Assert.assertTrue(user.isLoggedIn());
	}

	@Test(expectedExceptions = LoginException.class)
	public void testConcurrentLogins() throws Exception {
		SQLConnector connector = new SQLConnector(createTestConnection());
		String username = "TestAccount1";
		String password = "testPassword";
		connector.logInUser(username, password);
		// Concurrent login isn't aren't allowed.
		connector.logInUser(username, password);
	}

	@Test
	public void testLogOutUser() throws Exception {
		SQLConnector connector = new SQLConnector(createTestConnection());
		String username = "TestAccount1";
		String password = "testPassword";
		connector.logInUser(username, password);
		connector.logOutUser(username);
		User user = connector.lookUpUser(username);
		Assert.assertFalse(user.isLoggedIn());
	}

	@Test(expectedExceptions = LoginException.class)
	public void testInvalidPassword() throws Exception {
		SQLConnector connector = new SQLConnector(createTestConnection());
		String username = "TestAccount1";
		String password = "incorrectPassword";
		connector.logInUser(username, password);
	}
}
