package com.jenjinstudios.server.authentication.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Used to create test H2 databases for testing.
 *
 * @author Caleb Brinkman
 */
public final class TestConnectionFactory
{
	private final Collection<Connection> connections = new LinkedList<>();

	/**
	 * Create a test connection.
	 *
	 * @return A connection usable for testing.
	 *
	 * @throws Exception If there's an exception.
	 */
	public Connection createUserTestConnection() throws Exception {
		Class.forName("org.h2.Driver");
		String connectionUrl = "jdbc:h2:mem:jenjin_test" + UUID.randomUUID();
		Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
		Statement statement = testConnection.createStatement();
		statement.executeUpdate("CREATE TABLE JenjinUsers (" +
			  "  `username` VARCHAR(16) NOT NULL," +
			  "  `password` CHAR(64) NOT NULL," +
			  "  `salt` CHAR(48) NOT NULL," +
			  "  `loggedin` TINYINT NOT NULL DEFAULT '0'," +
			  "  PRIMARY KEY (username)" +
			  ')');
		for (int i = 1; i < 10; i++) {
			statement.executeUpdate(
				  "INSERT INTO JenjinUsers " +
						"(`username`, `password`, `salt`, `loggedin`)" +
						" VALUES " +
						"('TestAccount" + i + "', " +
						"'650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd', " +
						"'e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3', " +
						"'0')");
		}
		connections.add(testConnection);
		return testConnection;
	}

	/**
	 * Create a connection missing a column for testing.
	 *
	 * @return The connection.
	 *
	 * @throws Exception If there's an exception.
	 */
	public Connection createUserTestConnectionMissingColumn() throws Exception {
		Class.forName("org.h2.Driver");
		String connectionUrl = "jdbc:h2:mem:jenjin_test" + UUID.randomUUID();
		Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
		Statement statement = testConnection.createStatement();
		statement.executeUpdate("CREATE TABLE JenjinUsers (" +
			  "  `username` VARCHAR(16) NOT NULL," +
			  "  `password` CHAR(64) NOT NULL," +
			  "  `loggedin` TINYINT NOT NULL DEFAULT '0'," +
			  "  PRIMARY KEY (username)" +
			  ')');
		for (int i = 1; i < 10; i++) {
			statement.executeUpdate(
				  "INSERT INTO JenjinUsers " +
						"(`username`, `password`, `loggedin`)" +
						" VALUES " +
						"('TestAccount" + i + "', " +
						"'650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd', " +
						"'0')");
		}
		connections.add(testConnection);
		return testConnection;
	}

	/**
	 * Create a test connection with a duplicated user for testing purposes.
	 *
	 * @return The test connection.
	 *
	 * @throws Exception If there's an exception.
	 */
	public Connection createUserTestConnectionDuplicateUser() throws Exception {
		Class.forName("org.h2.Driver");
		String connectionUrl = "jdbc:h2:mem:jenjin_test" + UUID.randomUUID();
		Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
		Statement statement = testConnection.createStatement();
		// This means the table has no primary key... Uh, how?
		statement.executeUpdate("CREATE TABLE JenjinUsers (" +
			  "  `username` VARCHAR(16) NOT NULL," +
			  "  `password` CHAR(64) NOT NULL," +
			  "  `salt` CHAR(48) NOT NULL," +
			  "  `loggedin` TINYINT NOT NULL DEFAULT '0'," +
			  ')');
		for (int i = 1; i < 3; i++) {
			statement.executeUpdate(
				  "INSERT INTO JenjinUsers " +
						"(`username`, `password`, `salt`, `loggedin`)" +
						" VALUES " +
						"('TestAccount1', " +
						"'650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd', " +
						"'e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3', " +
						"'0')");
		}
		connections.add(testConnection);
		return testConnection;
	}

	/**
	 * Create a database connection for a user properties database.
	 *
	 * @return The test connection.
	 *
	 * @throws Exception If there's an exception when creating the connection.
	 */
	public Connection createPropertiesTestConnection() throws Exception {
		Class.forName("org.h2.Driver");
		String connectionUrl = "jdbc:h2:mem:jenjin_test" + UUID.randomUUID();
		Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
		Statement statement = testConnection.createStatement();
		// This means the table has no primary key... Uh, how?
		statement.executeUpdate("CREATE TABLE JenjinUserProperties (" +
			  "  `username` VARCHAR(16) NOT NULL," +
			  "  `propertyName` VARCHAR(64) NOT NULL," +
			  "  `value` VARCHAR(64) NOT NULL," +
			  " PRIMARY KEY (`username`, `propertyName`)" +
			  ')');
		for (int i = 1; i < 3; i++) {
			statement.executeUpdate(
				  "INSERT INTO JenjinUserProperties " +
						"(`username`, `propertyName`, `value`)" +
						" VALUES " +
						"('TestAccount1', " +
						"'property" + i + "', " +
						'\'' + i + "')");
		}
		connections.add(testConnection);
		return testConnection;
	}

	/**
	 * Create a database connection for a user properties database with invalid columns.
	 *
	 * @return The test connection.
	 *
	 * @throws Exception If there's an exception when creating the connection.
	 */
	public Connection createPropertiesTestConnectionInvalid() throws Exception {
		Class.forName("org.h2.Driver");
		String connectionUrl = "jdbc:h2:mem:jenjin_test" + UUID.randomUUID();
		Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
		Statement statement = testConnection.createStatement();
		// This means the table has no primary key... Uh, how?
		statement.executeUpdate("CREATE TABLE JenjinUserProperties (" +
			  "  `username` VARCHAR(16) NOT NULL," +
			  "  `value` VARCHAR(64) NOT NULL," +
			  ')');
		for (int i = 1; i < 3; i++) {
			statement.executeUpdate(
				  "INSERT INTO JenjinUserProperties " +
						"(`username`, `value`)" +
						" VALUES " +
						"('TestAccount1', " +
						'\'' + i + "')");
		}
		connections.add(testConnection);
		return testConnection;
	}

	/**
	 * Close all created test connections.
	 *
	 * @throws SQLException If there's an exception.
	 */
	public void closeAll() throws SQLException {
		for (Connection connection : connections) {
			connection.close();
		}
	}
}
