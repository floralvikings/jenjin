package com.jenjinstudios.world.server.database;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author Caleb Brinkman
 */
public class WorldAuthenticatorTest
{
	private static int connectionNumber = 0;

	private static Connection connection;

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
			  "  `xCoord` INT NOT NULL DEFAULT '0'," +
			  "  `yCoord` INT NOT NULL DEFAULT '0'," +
			  "  `zCoord` INT NOT NULL DEFAULT '0'," +
			  "  PRIMARY KEY (username)" +
			  ')');
		connectionNumber++;
		return testConnection;
	}

	@BeforeClass
	public void setUpConnection() throws Exception {
		connection = createTestConnection();
	}

	@AfterClass
	public void closeConnection() throws Exception {
		connection.close();
	}
}
