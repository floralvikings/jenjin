package com.jenjinstudios.demo.server;

import com.jenjinstudios.demo.server.net.DemoClientHandler;
import com.jenjinstudios.demo.server.net.DemoServer;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.server.sql.WorldAuthenticator;

import java.io.InputStream;
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
		DemoServer demoServer;

		demoServer = createWorldServer();
		demoServer.start();

		String readLine = input.nextLine();
		while (readLine != null && !"quit".equals(readLine))
		{
			Thread.sleep(100);
		}

		demoServer.shutdown();
	}

	private static DemoServer createWorldServer() throws Exception {
		ServerInit<DemoClientHandler> serverInit = new ServerInit<>(50, DemoClientHandler.class, 51015);
		Class.forName("org.h2.Driver");
		Connection sqlConnection = createDemoConnection();
		WorldAuthenticator worldAuthenticator = new WorldAuthenticator(sqlConnection);
		InputStream stream = Main.class.getClassLoader().
			  getResourceAsStream("com/jenjinstudios/demo/server/World.xml");
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(stream);
		return new DemoServer<>(serverInit, worldAuthenticator, worldDocumentReader);
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
			  "  `xcoord` DOUBLE NOT NULL DEFAULT '0'," +
			  "  `ycoord` DOUBLE NOT NULL DEFAULT '0'," +
			  "  `zoneid` INT(11) NOT NULL DEFAULT '0'," +
			  "  PRIMARY KEY (username)" +
			  ")");
		for (int i = 1; i < 100; i++)
		{
			statement.executeUpdate(
				  "INSERT INTO jenjin_users " +
						"(`username`, `password`, `salt`, `loggedin`, `xcoord`, `ycoord`, `zoneid`)" +
						" VALUES " +
						"('TestAccount" + i + "', " +
						"'650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd', " +
						"'e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3', " +
						"'0', '0', '0', '0')");
		}
		return testConnection;
	}
}
