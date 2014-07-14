package com.jenjinstudios.demo.server;

import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.net.ClientListenerInit;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.sql.WorldAuthenticator;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 * @author Caleb Brinkman
 */
public class Main
{
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		WorldServer worldServer;

		worldServer = createWorldServer(args[0], args[1], args[2]);
		worldServer.start();

		String readLine = input.nextLine();
		while (readLine != null && !"quit".equals(readLine))
		{
			Thread.sleep(100);
		}

		worldServer.shutdown();
	}

	public static WorldServer createWorldServer(String dbName, String dbUser, String dbPass) throws Exception {
		ClientListenerInit<WorldClientHandler> clientListenerInit =
			  new ClientListenerInit<>(WorldClientHandler.class, 51015);
		ServerInit<WorldClientHandler> serverInit =
			  new ServerInit<>(MessageRegistry.getInstance(), 10, clientListenerInit);
		Class.forName("org.h2.Driver");
		Connection sqlConnection = DriverManager.getConnection("jdbc:h2:" + dbName, dbUser, dbPass);
		DatabaseMetaData md = sqlConnection.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		while (rs.next())
		{
			System.out.println(rs.getString(3));
		}
		WorldAuthenticator worldAuthenticator = new WorldAuthenticator(sqlConnection);
		InputStream stream = Main.class.getClassLoader().
			  getResourceAsStream("com/jenjinstudios/demo/server/World.xml");
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(stream);
		return new WorldServer(serverInit, worldAuthenticator, worldDocumentReader);
	}
}
