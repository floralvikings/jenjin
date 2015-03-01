package com.jenjinstudios.server.database.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Used to create test H2 databases for testing.
 *
 * @author Caleb Brinkman
 */
public final class TestConnectionFactory
{
	private int connectionNumber;

	/**
	 * Create a unique connection with some dummy data that we can test on.
	 *
	 * @return The dummy connection.
	 *
	 * @throws Exception If something goes wrong creating the connection.
	 */
	public Connection createTestConnection() throws Exception {
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
		statement.executeUpdate("CREATE TABLE jenjin_user_properties (" +
			  " `username` VARCHAR(64) NOT NULL," +
			  " `propertyName` VARCHAR(64) NOT NULL," +
			  " `propertyValue` VARCHAR(64)," +
			  " PRIMARY KEY (`username`, `propertyName`))");
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
			statement.executeUpdate("INSERT INTO jenjin_user_properties (`username`, `propertyName`, " +
				  "`propertyValue`) " +
				  "VALUES ('TestAccount" + i + "', 'Foo', 'Bar')");
		}
		connectionNumber++;
		return testConnection;
	}
}
