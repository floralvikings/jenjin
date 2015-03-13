package com.jenjinstudios.demo.server;

import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.UserLookup;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.database.sql.PlayerTable;

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
		WorldServer demoServer;

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

	private static WorldServer createWorldServer() throws Exception {
		ServerInit serverInit = new ServerInit();
		serverInit.setHandlerClass(WorldClientHandler.class);
		Connection sqlConnection = createDemoConnection();
		UserLookup<Player> userLookup = new PlayerTable(sqlConnection);
		Authenticator<Player> worldAuthenticator = new Authenticator<>(userLookup);
		return new WorldServer(serverInit, worldAuthenticator, null);
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
			  "  `xCoord` DOUBLE NOT NULL DEFAULT '0'," +
			  "  `yCoord` DOUBLE NOT NULL DEFAULT '0'," +
			  "  `zoneId` INT NOT NULL DEFAULT '0'," +
			  "  PRIMARY KEY (username)" +
			  ')');
		for (int i = 1; i < 100; i++)
		{
			statement.executeUpdate(
				  "INSERT INTO jenjin_users " +
						"(`username`, `password`, `salt`, `loggedin`, `xCoord`, `yCoord`, `zoneId`)" +
						" VALUES " +
						"('TestAccount" + i + "', " +
						"'650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd', " +
						"'e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3', " +
						"'0', '0', '0', '0')");
		}
		return testConnection;
	}
}
