package com.jenjinstudios.demo.server;

import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.net.ClientListenerInit;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.sql.WorldAuthenticator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.LogManager;

/**
 * @author Caleb Brinkman
 */
public class Main
{
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		WorldServer worldServer;

		setUpLoggers();

		worldServer = createWorldServer(args[0], args[1], args[2]);
		worldServer.start();

		String readLine = input.nextLine();
		while (readLine != null && !"quit".equals(readLine))
		{
			Thread.sleep(100);
		}

		worldServer.shutdown();
	}

	public static void setUpLoggers() throws IOException {
		Properties preferences = new Properties();
		InputStream stream = Main.class.getClassLoader().
			  getResourceAsStream("com/jenjinstudios/demo/server/logger.properties");
		preferences.load(stream);
		LogManager.getLogManager().readConfiguration(stream);
	}

	public static WorldServer createWorldServer(String dbName, String dbUser, String dbPass) throws Exception {
		ClientListenerInit<WorldClientHandler> clientListenerInit =
			  new ClientListenerInit<>(WorldClientHandler.class, 51015);
		ServerInit<WorldClientHandler> serverInit =
			  new ServerInit<>(MessageRegistry.getInstance(), 10, clientListenerInit);
		Class.forName("org.h2.Driver");
		Connection sqlConnection = DriverManager.getConnection("jdbc:h2:" + dbName, dbUser, dbPass);
		WorldAuthenticator worldAuthenticator = new WorldAuthenticator(sqlConnection);
		InputStream stream = Main.class.getClassLoader().
			  getResourceAsStream("com/jenjinstudios/demo/server/World.xml");
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(stream);
		return new WorldServer(serverInit, worldAuthenticator, worldDocumentReader);
	}
}
