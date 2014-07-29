package com.jenjinstudios.world.server.sql;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;

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
		WorldAuthenticator worldAuthenticator = new WorldAuthenticator(connection);
		Actor actor = mock(Actor.class);
		when(actor.getName()).thenReturn("TestAccount1");
		when(actor.getVector2D()).thenReturn(new Vector2D(10, 10));

		worldAuthenticator.updatePlayer(actor);

		String query = "SELECT * FROM jenjin_users WHERE username='TestAccount1'";
		PreparedStatement statement = connection.prepareStatement(query, TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE);
		ResultSet results = statement.executeQuery();
		results.next();
		Vector2D coordinates = new Vector2D(results.getDouble("xCoord"), results.getDouble("yCoord"));
		assertEquals(coordinates, new Vector2D(10, 10));
	}
}
