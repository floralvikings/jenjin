package com.jenjinstudios.world.server.sql;

import com.jenjinstudios.server.sql.Authenticator;
import com.jenjinstudios.world.Actor;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles SQL stuff for a WorldServer.
 * @author Caleb Brinkman
 */
@SuppressWarnings("SpellCheckingInspection")
public class WorldAuthenticator extends Authenticator
{
	/** The column name of the X coordinate. */
	private static final String X_COORD = "xCoord";
	/** The column name of the Y coordinate. */
	private static final String Y_COORD = "yCoord";
	/** The column name of the zone ID. */
	private static final String ZONE_ID = "zoneID";
	/** The Logger used for this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldAuthenticator.class.getName());

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 */
	public WorldAuthenticator(Connection connection) { super(connection); }

	public Map<String, Object> getPlayerInfo(String username) {
		HashMap<String, Object> playerData = new HashMap<>();
		try (ResultSet results = makeUserQuery(username))
		{
			ResultSetMetaData metaData = results.getMetaData();
			results.next();
			for (int i = 0; i < metaData.getColumnCount(); i++)
			{
				playerData.put(metaData.getColumnName(i), results.getObject(i));
			}
		} catch (SQLException | IndexOutOfBoundsException e)
		{
			LOGGER.log(Level.FINE, "Failed to get user data: " + username, e);
		}
		return playerData;
	}

	/**
	 * Update the coordinates of the given actor in the database.
	 * @param player The player to update.
	 */
	public boolean updatePlayer(Actor player) {
		boolean success = false;
		String username = player.getName();
		double xCoord = player.getVector2D().getXCoordinate();
		double yCoord = player.getVector2D().getYCoordinate();
		int zoneId = player.getZoneID();

		String updatePlayerQuery = "UPDATE jenjin_users SET " + X_COORD + "=" + xCoord + ", " + Y_COORD +
			  "=" + yCoord + ", " + ZONE_ID + "=" + zoneId + " WHERE " + "username = ?";
		synchronized (dbConnection)
		{
			try (PreparedStatement updatePlayerStatement = dbConnection.prepareStatement(updatePlayerQuery))
			{
				updatePlayerStatement.setString(1, username);
				updatePlayerStatement.executeUpdate();
				updatePlayerStatement.close();
				success = true;
			} catch (SQLException e)
			{
				LOGGER.log(Level.WARNING, "Unable to log out player.", e);
			}
		}
		return success;
	}
}
