package com.jenjinstudios.world.server.sql;

import com.jenjinstudios.server.net.User;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.WorldClientHandler;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.Map;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

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
				  "('TestAccount" + i + "', 'zoneID', '0')," +
				  "('TestAccount" + i + "', 'Foo', 'Bar') ");
		}
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

	@Test
	public void testUpdatePlayer() throws Exception {
		WorldClientHandler worldClientHandler = Mockito.mock(WorldClientHandler.class);
		WorldAuthenticator worldAuthenticator = new WorldAuthenticator(connection);
		Actor actor = mock(Actor.class);
		User user = new User();
		user.setUsername("TestAccount1");
		when(actor.getVector2D()).thenReturn(new Vector2D(10, 10));
		when(worldClientHandler.getPlayer()).thenReturn(actor);
		when(worldClientHandler.getUser()).thenReturn(user);

		worldAuthenticator.updatePlayer(worldClientHandler);

		String query = "SELECT * FROM jenjin_user_properties WHERE username='TestAccount1' AND propertyName='xCoord'";
		PreparedStatement statement = connection.prepareStatement(query, TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE);
		ResultSet results = statement.executeQuery();
		results.next();
		assertEquals(results.getDouble("propertyValue"), 10d);
	}

	@Test
	public void testLookUpUserProperties() throws Exception {
		WorldAuthenticator authenticator = new WorldAuthenticator(connection);
		String username = "TestAccount1";
		Map<String, Object> properties = authenticator.lookUpUserProperties(username);
		Assert.assertEquals(properties.get("Foo"), "Bar");
	}

	@Test
	public void testLookUpUserProperty() throws Exception {
		WorldAuthenticator authenticator = new WorldAuthenticator(connection);
		String username = "TestAccount1";
		Object foo = authenticator.lookUpUserProperty(username, "Foo");
		Assert.assertEquals(foo, "Bar");
	}

	@Test
	public void testInsertNewProperty() throws Exception {
		WorldAuthenticator authenticator = new WorldAuthenticator(connection);
		User user = authenticator.getUserLookup().findUser("TestAccount1");
		user.getProperties().put("Donkey", "Hotey");
		authenticator.updateUserProperties(user);

		Object o = authenticator.lookUpUserProperty("TestAccount1", "Donkey");
		Assert.assertEquals(o, "Hotey");
	}

	@Test
	public void testUpdateProperty() throws Exception {
		WorldAuthenticator authenticator = new WorldAuthenticator(connection);
		User user = authenticator.getUserLookup().findUser("TestAccount1");
		user.getProperties().put("Foo", "Hotey");
		authenticator.updateUserProperties(user);

		Object o = authenticator.lookUpUserProperty("TestAccount1", "Foo");
		Assert.assertEquals(o, "Hotey");
	}
}
