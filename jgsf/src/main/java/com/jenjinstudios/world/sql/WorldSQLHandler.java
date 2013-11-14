package com.jenjinstudios.world.sql;

import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.sql.SQLHandler;
import com.jenjinstudios.util.Hash;
import com.jenjinstudios.world.Actor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles SQL stuff for a WorldServer.
 * @author Caleb Brinkman
 */
public class WorldSQLHandler extends SQLHandler
{
	/** The column name of the X coordinate. */
	public static final String X_COORD = "xCoord";
	/** The column name of the Z coordinate. */
	public static final String Z_COORD = "zCoord";
	/** The Logger used for this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldSQLHandler.class.getName());

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 * @param dbAddress The URL of the database, in the format www.example.place
	 * @param dbName The name of the database.
	 * @param dbUsername The username used to access the database.
	 * @param dbPassword The password used to access the database
	 * @throws java.sql.SQLException If there is an issue connecting to the database.
	 */
	public WorldSQLHandler(String dbAddress, String dbName, String dbUsername, String dbPassword) throws SQLException {
		super(dbAddress, dbName, dbUsername, dbPassword);
	}

	/**
	 * Log the player into the world.
	 * @param username The player's username.
	 * @param password The player's password.
	 * @return An actor pre-filled with the players information.
	 */
	public synchronized Actor logInPlayer(String username, String password) {
		Actor player = null;
		if (!isConnected())
			return player;
		try
		{
			ResultSet results = makeUserQuery(username);
			results.next();
			// Determine if the user is logged in.  If yes, end of method.
			boolean loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			if (loggedIn)
				return player;
			// Hash the user-supplied password with the salt in the database.
			String hashedPassword = Hash.getHashedString(password, results.getString("salt"));
			// Determine if the correct password was supplied.
			boolean passwordCorrect = hashedPassword.equalsIgnoreCase(results.getString("password"));
			Vector2D coordinates = new Vector2D(results.getDouble(X_COORD), results.getDouble(Z_COORD));
			// Any SQL stuff has to come before this line.
			results.getStatement().close();
			// If the password's bad, login fail.
			if (!passwordCorrect)
				return player;
			// Update the logged in column.
			updateLoggedinColumn(username, true);
			player = new Actor(username);
			player.setVector2D(coordinates);
		} catch (SQLException | IndexOutOfBoundsException e)
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
		if (!isConnected())
			return success;
		String username = actor.getName();
		try // TODO Make sure error is handled gracefully
		{
			ResultSet results = makeUserQuery(username);
			results.next();
			// Determine if the user is logged in.  If no, end of method.
			boolean loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			results.getStatement().close();
			if (!loggedIn)
				return success;

			updateLoggedinColumn(username, false);
			updatePlayer(actor);
			success = true;
		} catch (SQLException e)
		{
			LOGGER.log(Level.FINE, "Failed to log out user: {0}", username);
			success = false;
		}

		return success;
	}

	/**
	 * Update the coordinates of the given actor in the database.
	 * @param player The player to update.
	 * @throws SQLException If there's a SQL exception.
	 */
	public void updatePlayer(Actor player) throws SQLException {
		String username = player.getName();
		double xCoord = player.getVector2D().getXCoordinate();
		double zCoord = player.getVector2D().getZCoordinate();

		String updateLoggedInQuery = "UPDATE " + dbName + ".users SET " + X_COORD + "=" + xCoord + ", " + Z_COORD +
				"=" + zCoord + " WHERE " + "username = ?";
		PreparedStatement updatePlayerStatement;
		updatePlayerStatement = super.dbConnection.prepareStatement(updateLoggedInQuery);
		updatePlayerStatement.setString(1, username);
		updatePlayerStatement.executeUpdate();
		updatePlayerStatement.close();
	}
}
