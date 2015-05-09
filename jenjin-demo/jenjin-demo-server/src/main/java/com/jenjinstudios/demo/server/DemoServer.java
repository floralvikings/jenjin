package com.jenjinstudios.demo.server;

import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.sql.JenjinUserSqlLookup;
import com.jenjinstudios.server.authentication.sql.JenjinUserSqlUpdate;
import com.jenjinstudios.server.authentication.sql.UserPropertiesSqlLookup;
import com.jenjinstudios.server.authentication.sql.UserPropertiesSqlUpdate;
import com.jenjinstudios.server.database.DatabaseLookup;
import com.jenjinstudios.server.database.DatabaseUpdate;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.WorldServerMessageContext;
import com.jenjinstudios.world.server.authentication.FullPlayerLookup;
import com.jenjinstudios.world.server.authentication.FullPlayerUpdate;
import com.jenjinstudios.world.server.authentication.PlayerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Demonstrates a very simple game server using the Jenjin WorldServer.
 *
 * @author Caleb Brinkman
 */
public final class DemoServer
{
	private static final Logger LOGGER = Logger.getLogger(DemoServer.class.getName());

	private DemoServer() {}

	/**
	 * Begin running the demo; the demo will run until the "quit" command is entered at the console.
	 *
	 * @param args Ignored.
	 *
	 * @throws IOException If there is an exception caused by the demo.
	 */
	public static void main(String... args) throws IOException {
		InputStream stream = DemoServer.class.getResourceAsStream("/com/jenjinstudios/demo/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Demo Client Messages", stream);
		Scanner input = new Scanner(System.in);

		WorldServer demoServer = createWorldServer();
		if (demoServer != null) {
			demoServer.start();

			String readLine = input.nextLine();
			while ((readLine != null) && !"quit".equals(readLine)) {
				readLine = input.nextLine();
			}

			demoServer.shutdown();
		}
	}

	private static WorldServer createWorldServer() {
		ServerInit<WorldServerMessageContext> serverInit = new ServerInit<>();
		serverInit.setContextClass(WorldServerMessageContext.class);
		WorldServer ws = null;
		try {
			Connection sqlConnection = createDemoConnection();
			DatabaseLookup<Map<String, String>> propertiesLookup = new UserPropertiesSqlLookup(sqlConnection);
			DatabaseLookup<Player> playerLookup = new JenjinUserSqlLookup<>(new PlayerFactory(), sqlConnection);
			DatabaseLookup<Player> fullPlayerLookup = new FullPlayerLookup<>(playerLookup, propertiesLookup);
			DatabaseUpdate<Map<String, String>> propertiesUpdate = new UserPropertiesSqlUpdate(sqlConnection);
			DatabaseUpdate<Player> playerUpdate = new JenjinUserSqlUpdate<>(sqlConnection);
			DatabaseUpdate<Player> fullPlayerUpdate = new FullPlayerUpdate<>(playerUpdate, propertiesUpdate);
			Authenticator<Player> worldAuthenticator = new Authenticator<>(fullPlayerLookup, fullPlayerUpdate);
			InputStream stream = DemoServer.class.getClassLoader().getResourceAsStream
				  ("com/jenjinstudios/demo/server/World.json");
			ws = new WorldServer<>(serverInit, worldAuthenticator, new WorldDocumentReader(stream));
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, "org.h2.Driver class not loaded.", e);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "SQL Exception when initializing demo database.", e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IOException when creating world server.", e);
		}

		return ws;
	}

	private static Connection createDemoConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		String connectionUrl = "jdbc:h2:mem:jenjin_test";
		Connection testConnection = DriverManager.getConnection(connectionUrl, "sa", "");
		Statement statement = testConnection.createStatement();
		statement.executeUpdate("CREATE TABLE JenjinUsers (" +
			  "  `username` VARCHAR(16) NOT NULL," +
			  "  `password` CHAR(64) NOT NULL," +
			  "  `salt` CHAR(48) NOT NULL," +
			  "  `loggedin` TINYINT NOT NULL DEFAULT '0'," +
			  "  PRIMARY KEY (username)" +
			  ')');
		statement.executeUpdate("CREATE TABLE JenjinUserProperties (" +
			  " `username` VARCHAR(16) NOT NULL," +
			  " `propertyName` VARCHAR(64) NOT NULL," +
			  " `value` VARCHAR(64) NOT NULL, " +
			  " PRIMARY KEY (username, propertyName)" +
			  ')');
		for (int i = 1; i < 100; i++) {
			statement.executeUpdate(
				  "INSERT INTO JenjinUsers " +
						"(`username`, `password`, `salt`, `loggedin`)" +
						" VALUES " +
						"('TestAccount" + i + "', " +
						"'650f00f552d4df0147d236e240ccfc490444f4b358c4ff1d79f5fd90f57243bd', " +
						"'e3c42b85a183d3f654a3d2bb3bc5ea607d0fb529d9b890d3', " +
						"'0')");
			statement.executeUpdate("INSERT INTO JenjinUserProperties " +
				  "(`username`, `propertyName`, `value`) " +
				  "VALUES " +
				  "('TestAccount" + i + "', 'xCoord', '0.0'), " +
				  "('TestAccount" + i + "', 'yCoord', '0.0'), " +
				  "('TestAccount" + i + "', 'zoneId', '0')");
		}
		return testConnection;
	}
}
