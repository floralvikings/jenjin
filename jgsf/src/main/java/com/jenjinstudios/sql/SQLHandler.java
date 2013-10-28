package com.jenjinstudios.sql;

import com.jenjinstudios.util.Hash;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;

/**
 * The SQLHandler class is responsible for connecting to and querying the SQL database associated with a given
 * SqlEnabledServer.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class SQLHandler extends SQLConnector
{

	/** The name of the column in the user table specifying whether the user is currently logged in. */
	public static final String LOGGED_IN_COLUMN = "loggedin";
	/** The Logger used for this class. */
	private static final Logger LOGGER = Logger.getLogger(SQLHandler.class.getName());

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 *
	 * @param dbAddress  The URL of the database, in the format www.example.place
	 * @param dbName     The name of the database.
	 * @param dbUsername The username used to access the database.
	 * @param dbPassword The password used to access the database
	 *
	 * @throws SQLException If there is an issue connecting to the database.
	 */
	public SQLHandler(String dbAddress, String dbName, String dbUsername, String dbPassword) throws SQLException
	{
		super(dbAddress, dbName, dbUsername, dbPassword);
	}

	/**
	 * Attempt to log the given user with the given password into the database.  This method does not perform any sort of
	 * hashing or encryption on the password.  If the user is already logged in this method will return false.
	 * <p/>
	 * This method should be overwritten by implementations, or called from super if they still wish to use the "loggedIn"
	 * column.
	 *
	 * @param username The username of the user to be logged in.
	 * @param password The password of the user to be logged in.
	 *
	 * @return true if the user was logged in successfully, false if the user was already logged in or the update to the
	 *         database failed.
	 */
	public synchronized boolean logInUser(String username, String password)
	{
		boolean success = false;
		if (!isConnected())
			return success;
		try
		{
			ResultSet results = makeUserQuery(username);
			results.next();
			// Determine if the user is logged in.  If yes, end of method.
			boolean loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			if (loggedIn)
				return success;
			// Hash the user-supplied password with the salt in the database.
			String hashedPassword = Hash.getHashedString(password, results.getString("salt"));
			// Determine if the correct password was supplied.
			boolean passwordCorrect = hashedPassword.equalsIgnoreCase(results.getString("password"));
			results.getStatement().close();
			if (!passwordCorrect)
				return success;

			updateLoggedinColumn(username, true);
			success = true;
		} catch (SQLException | IndexOutOfBoundsException e)
		{
			LOGGER.log(Level.FINE, "Failed to log in user: {0}", username);
			success = false;
		}
		return success;
	}

	/**
	 * Attempt to log out the given user with the given password into the database.  This method does not perform any sort
	 * of hashing or encryption on the password.  If the user is already logged in this method will return false.
	 *
	 * @param username The username of the user to be logged out.
	 *
	 * @return true if the user was logged out successfully, false if the user was already logged out or the update to the
	 *         database failed.
	 */
	public synchronized boolean logOutUser(String username)
	{
		boolean success = false;
		if (!isConnected())
			return success;
		try
		{
			ResultSet results = makeUserQuery(username);
			results.next();
			// Determine if the user is logged in.  If no, end of method.
			boolean loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			results.getStatement().close();
			if (!loggedIn)
				return success;

			updateLoggedinColumn(username, false);
			success = true;
		} catch (SQLException | IndexOutOfBoundsException e)
		{
			LOGGER.log(Level.FINE, "Failed to log out user: {0}", username);
			success = false;
		}
		return success;
	}

	/**
	 * Query the database for user info.
	 *
	 * @param username The username of the user we're looking for.
	 *
	 * @return The ResultSet returned by the query.
	 *
	 * @throws SQLException If there is a SQL error.
	 */
	protected ResultSet makeUserQuery(String username) throws SQLException
	{
		PreparedStatement statement = getDbConnection().prepareStatement(getUserQuery(), TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE);
		statement.setString(1, username);
		return statement.executeQuery();
	}

	/**
	 * Update the loggedin column to reflect the supplied boolean.
	 *
	 * @param username The user being queried.
	 * @param status   The new status of the loggedin column.
	 *
	 * @throws SQLException If there is a SQL error.
	 */
	protected void updateLoggedinColumn(String username, boolean status) throws SQLException
	{
		String newValue = status ? "1" : "0";
		String updateLoggedInQuery = "UPDATE " + dbName + ".users SET " + LOGGED_IN_COLUMN + "=" + newValue + " WHERE " +
				"username = ?";
		PreparedStatement updateLoggedin;
		updateLoggedin = getDbConnection().prepareStatement(updateLoggedInQuery);
		updateLoggedin.setString(1, username);
		updateLoggedin.executeUpdate();
		updateLoggedin.close();
	}
}
