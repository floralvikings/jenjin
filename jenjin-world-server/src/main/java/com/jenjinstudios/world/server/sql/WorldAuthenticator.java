package com.jenjinstudios.world.server.sql;

import com.jenjinstudios.core.util.Hash;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.sql.Authenticator;
import com.jenjinstudios.server.sql.LoginException;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	/**
	 * Log the player into the world.
	 * @return An actor pre-filled with the players information.
	 */
	public Player logInPlayer(User user) {
		String username = user.getUsername();
		String password = user.getPassword();
		Player player = null;
		try (ResultSet results = makeUserQuery(username))
		{
			results.next();
			// Determine if the user is logged in.  If yes, end of method.
			boolean loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			if (!loggedIn)
			{
				// Hash the user-supplied password with the salt in the database.
				String hashedPassword = Hash.getHashedString(password, results.getString("salt"));
				// Determine if the correct password was supplied.
				boolean passwordCorrect = hashedPassword != null && hashedPassword.equalsIgnoreCase(results.getString
					  ("password"));
				Vector2D coordinates = new Vector2D(results.getDouble(X_COORD), results.getDouble(Y_COORD));
				int zoneID = results.getInt(ZONE_ID);
				// If the password's bad, login fail.
				if (passwordCorrect)
				{
					// Update the logged in column.
					updateLoggedinColumn(username, true);
					player = new Player(username);
					player.setVector2D(coordinates);
					player.setZoneID(zoneID);
				}
			}
		} catch (SQLException | IndexOutOfBoundsException | LoginException e)
		{
			LOGGER.log(Level.FINE, "Failed to log in user: " + username, e);
			player = null;
		}
		return player;
	}

	/**
	 * Log a player out of the world, storing their coordinates in the database.
	 * @param actor The actor to be logged out of the world.
	 * @return Whether the actor was successfully logged out.
	 */
	public boolean logOutPlayer(Actor actor) {
		boolean success = false;
		String username = actor.getName();
		try (ResultSet results = makeUserQuery(username))
		{
			results.next();
			// Determine if the user is logged in.  If no, end of method.
			boolean loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			if (loggedIn)
			{

				updateLoggedinColumn(username, false);
				success = true;
			}
		} catch (SQLException | LoginException e)
		{
			LOGGER.log(Level.WARNING, "Failed to log out user: {0}", username);
			success = false;
		}

		return success;
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

		String updatePlayerQuery = "UPDATE users SET " + X_COORD + "=" + xCoord + ", " + Y_COORD +
			  "=" + yCoord + " WHERE " + "username = ?";
		synchronized (dbConnection)
		{
			try (PreparedStatement updatePlayerStatement = super.dbConnection.prepareStatement(updatePlayerQuery))
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
