package com.jenjinstudios.jgsf.world.sql;

import com.jenjinstudios.sql.SQLHandler;
import com.jenjinstudios.util.Hash;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles SQL stuff for a WorldServer.
 *
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
	 *
	 * @param dbAddress  The URL of the database, in the format www.example.place
	 * @param dbName     The name of the database.
	 * @param dbUsername The username used to access the database.
	 * @param dbPassword The password used to access the database
	 * @throws java.sql.SQLException If there is an issue connecting to the database.
	 */
	public WorldSQLHandler(String dbAddress, String dbName, String dbUsername, String dbPassword) throws SQLException
	{
		super(dbAddress, dbName, dbUsername, dbPassword);
	}

	/**
	 * Log the player into the world.
	 *
	 * @param username The player's username.
	 * @param password The player's password.
	 * @return A {@code TreeMap} containing the player's information.
	 */
	public synchronized TreeMap<String, Object> logIntoWorld(String username, String password)
	{
		TreeMap<String, Object> playerInfo = null;
		if (!isConnected())
			return playerInfo;
		try
		{
			ResultSet results = makeUserQuery(username);
			results.next();
			// Determine if the user is logged in.  If yes, end of method.
			boolean loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			if (loggedIn)
				return playerInfo;
			// Hash the user-supplied password with the salt in the database.
			String hashedPassword = Hash.getHashedString(password, results.getString("salt"));
			// Determine if the correct password was supplied.
			boolean passwordCorrect = hashedPassword.equalsIgnoreCase(results.getString("password"));
			results.getStatement().close();
			if (!passwordCorrect)
				return playerInfo;

			updateLoggedinColumn(username, true);
			playerInfo = new TreeMap<>();
			playerInfo.put(X_COORD, results.getFloat(X_COORD));
			playerInfo.put(Z_COORD, results.getFloat(Z_COORD));
		} catch (SQLException | IndexOutOfBoundsException e)
		{
			LOGGER.log(Level.FINE, "Failed to log in user: {0}", username);
			playerInfo = null;
		}
		return playerInfo;
	}
}
