package com.jenjinstudios.demo.server;

import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.sql.WorldAuthenticator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

/**
 * @author Caleb Brinkman
 */
public class Main
{
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		WorldServer<WorldClientHandler> demoServer;

		demoServer = createWorldServer();
		demoServer.start();

		String readLine = input.nextLine();
		while (readLine != null && !"quit".equals(readLine))
		{
			Thread.sleep(100);
			readLine = input.nextLine();
		}

		demoServer.shutdown();
	}

	private static WorldServer<WorldClientHandler> createWorldServer() throws Exception {
		ServerInit<WorldClientHandler> serverInit = new ServerInit<>();
		serverInit.setUps(50);
		serverInit.setHandlerClass(WorldClientHandler.class);
		serverInit.setPort(51015);
		Connection sqlConnection = createDemoConnection();
		WorldAuthenticator worldAuthenticator = new WorldAuthenticator(sqlConnection);
		return new WorldServer<>(serverInit, worldAuthenticator, null);
	}

	private static Connection createDemoConnection() throws Exception {
		Class.forName("org.h2.Driver");
		String connectionUrl = "jdbc:h2:mem:jenjin_test";
		Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
		Statement statement = testConnection.createStatement();
		statement.executeUpdate("CREATE TABLE jenjin_users (" +
			  "  `username` VARCHAR(16) NOT NULL," +
			  "  `password` CHAR(64) NOT NULL," +
			  "  `salt` CHAR(48) NOT NULL," +
			  "  `loggedin` TINYINT NOT NULL DEFAULT '0'," +
			  "  PRIMARY KEY (username)" +
			  ")");
		statement.executeUpdate("CREATE TABLE jenjin_user_properties (" +
			  " `username` VARCHAR(64) NOT NULL," +
			  " `propertyName` VARCHAR(64) NOT NULL," +
			  " `propertyValue` VARCHAR(64)," +
			  " PRIMARY KEY (`username`, `propertyName`))");
		for (int i = 1; i < 100; i++)
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
				  "VALUES " +
				  "('TestAccount" + i + "', 'xCoord', '0'), " +
				  "('TestAccount" + i + "', 'yCoord', '0'), " +
				  "('TestAccount" + i + "', 'zoneID', '0') ");
		}
		return testConnection;
	}
}
