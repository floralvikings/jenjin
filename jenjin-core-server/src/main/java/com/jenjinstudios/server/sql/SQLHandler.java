package com.jenjinstudios.server.sql;

import com.jenjinstudios.core.util.Hash;
import com.jenjinstudios.server.net.User;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;

/**
 * The SQLHandler class is responsible for connecting to and querying the SQL database associated with a given Server.
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class SQLHandler
{

	/** The name of the column in the user table specifying whether the user is currently logged in. */
	public static final String LOGGED_IN_COLUMN = "loggedin";
	/** The Logger used for this class. */
	private static final Logger LOGGER = Logger.getLogger(SQLHandler.class.getName());
	/** The connection used to communicate with the SQL database. */
	protected final Connection dbConnection;
	/** The string used to get all information about the user. */
	private final String USER_QUERY;

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 */
	public SQLHandler(Connection dbConnection) {
		USER_QUERY = "SELECT * FROM users WHERE username = ?";
		this.dbConnection = dbConnection;
	}

	/**
	 * Attempt to log the given user with the given password into the database.  This method does not perform any sort
	 * of hashing or encryption on the password.  If the user is already logged in this method will return false.
	 * <p/>
	 * This method should be overwritten by implementations, or called from super if they still wish to use the
	 * "loggedIn" column.
	 * @return true if the user was logged in successfully, false if the user was already logged in or the update to the
	 * database failed.
	 */
	public boolean logInUser(User user) {
		String username = user.getUsername();
		String password = user.getPassword();
		boolean success;
		try (ResultSet results = makeUserQuery(username))
		{
			results.next();
			// Determine if the user is logged in.  If yes, end of method.
			boolean loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			if (loggedIn)
				return false;
			// Hash the user-supplied password with the salt in the database.
			String hashedPassword = Hash.getHashedString(password, results.getString("salt"));
			// Determine if the correct password was supplied.
			boolean passwordCorrect = hashedPassword != null && hashedPassword.equalsIgnoreCase(results.getString("password"));
			if (!passwordCorrect)
				return false;

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
	 * Attempt to log out the given user with the given password into the database.  This method does not perform any
	 * sort of hashing or encryption on the password.  If the user is already logged in this method will return false.
	 * @param username The username of the user to be logged out.
	 * @return true if the user was logged out successfully, false if the user was already logged out or the update to
	 * the database failed.
	 */
	public boolean logOutUser(String username) {
		boolean success;
		try (ResultSet results = makeUserQuery(username))
		{
			results.next();
			// Determine if the user is logged in.  If no, end of method.
			boolean loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			if (!loggedIn)
				return false;

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
	 * @param username The username of the user we're looking for.
	 * @return The ResultSet returned by the query.
	 * @throws SQLException If there is a SQL error.
	 */
	protected ResultSet makeUserQuery(String username) throws SQLException {
		synchronized (dbConnection)
		{
			@SuppressWarnings("resource") // Need to suppress warning, result set must be closed by calling method.
					PreparedStatement statement = dbConnection.prepareStatement(USER_QUERY, TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE);
			statement.setString(1, username);
			return statement.executeQuery();
		}
	}

	/**
	 * Update the loggedin column to reflect the supplied boolean.
	 * @param username The user being queried.
	 * @param status The new status of the loggedin column.
	 * @throws SQLException If there is a SQL error.
	 */
	protected void updateLoggedinColumn(String username, boolean status) throws SQLException {
		String newValue = status ? "1" : "0";
		String updateLoggedInQuery = "UPDATE users SET " + LOGGED_IN_COLUMN + "=" + newValue + " WHERE " +
				"username = ?";
		synchronized (dbConnection)
		{
			try (PreparedStatement updateLoggedIn = dbConnection.prepareStatement(updateLoggedInQuery))
			{
				updateLoggedIn.setString(1, username);
				updateLoggedIn.executeUpdate();
				updateLoggedIn.close();
			}
		}
	}
}
