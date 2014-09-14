package com.jenjinstudios.server.sql;

import com.jenjinstudios.core.util.Hash;
import com.jenjinstudios.server.net.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;

/**
 * The SQLHandler class is responsible for connecting to and querying the SQL database associated with a given Server.
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class Authenticator
{
	private static final String SALT_COLUMN = "salt";
	private static final String PASSWORD_COLUMN = "password";
	/** The name of the column in the user table specifying whether the user is currently logged in. */
	private static final String LOGGED_IN_COLUMN = "loggedin";
	/** The connection used to communicate with the SQL database. */
	protected final Connection dbConnection;
	/** The string used to get all information about the user. */
	private final String USER_QUERY;

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 */
	public Authenticator(Connection dbConnection) {
		USER_QUERY = "SELECT * FROM jenjin_users WHERE username = ?";
		this.dbConnection = dbConnection;
	}

	/**
	 * Attempt to log the given user with the given password into the database.  This method does not perform any sort
	 * of hashing or encryption on the password.  If the user is already logged in this method will return false.
	 * <p>
	 * This method should be overwritten by implementations, or called from super if they still wish to use the
	 * "loggedIn" column.
	 * @return true if the user was logged in successfully, false if the user was already logged in or the update to
	 * the
	 * database failed.
	 */
	public User logInUser(String username, String password) throws LoginException {
		User user = getUserWithValidPassword(username, password);
		updateLoggedinColumn(username, true);
		user.setLoggedIn(true);
		return user;
	}

	private User getUserWithValidPassword(String username, String password) throws LoginException {
		User user = lookUpUser(username);
		if (user.isLoggedIn())
			throw new LoginException("User " + username + " is already logged in.");
		String hashedPassword = Hash.getHashedString(password, user.getSalt());
		boolean passwordCorrect = hashedPassword != null && hashedPassword.equalsIgnoreCase(user.getPassword());
		if (!passwordCorrect)
			throw new LoginException("User " + username + " provided incorrect password.");
		return user;
	}

	public User lookUpUser(String username) throws LoginException {
		boolean loggedIn;
		String salt;
		String dbPass;
		User user;
		try (ResultSet results = makeUserQuery(username))
		{
			if (!results.next())
			{
				throw new LoginException("User " + username + " does not exist.");
			}
			loggedIn = results.getBoolean(LOGGED_IN_COLUMN);
			salt = results.getString(SALT_COLUMN);
			dbPass = results.getString(PASSWORD_COLUMN);
			user = new User();
			user.setUsername(username);
			user.setPassword(dbPass);
			user.setSalt(salt);
			user.setLoggedIn(loggedIn);
		} catch (SQLException e)
		{
			throw new LoginException("Unable to retrieve user " + username + " because of SQL Exception.", e);
		}
		return user;
	}

	/**
	 * Attempt to log out the user with the given username.  Note that if a user is already logged out,
	 * this method will
	 * have no affect.
	 * @param username The username of the user to be logged out.
	 * @return The user that was logged out.
	 */
	public User logOutUser(String username) throws LoginException {
		User user = lookUpUser(username);
		if (user.isLoggedIn())
		{
			user.setLoggedIn(false);
			updateLoggedinColumn(username, false);
		}
		return user;
	}

	/**
	 * Query the database for user info.
	 * @param username The username of the user we're looking for.
	 * @return The ResultSet returned by the query.
	 * @throws SQLException If there is a SQL error.
	 */
	protected ResultSet makeUserQuery(String username) throws SQLException {
		PreparedStatement statement;
		synchronized (dbConnection)
		{
			statement = dbConnection.prepareStatement(USER_QUERY,
				  TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE);
			statement.setString(1, username);

		}
		return statement.executeQuery();
	}

	/**
	 * Update the loggedin column to reflect the supplied boolean.
	 * @param username The user being queried.
	 * @param status The new status of the loggedin column.
	 * @throws com.jenjinstudios.server.sql.LoginException If there is a SQL error.
	 */
	protected void updateLoggedinColumn(String username, boolean status) throws LoginException {
		String newValue = status ? "1" : "0";
		String updateLoggedInQuery = "UPDATE jenjin_users SET " + LOGGED_IN_COLUMN + "=" + newValue + " WHERE " +
			  "username = ?";
		synchronized (dbConnection)
		{
			try (PreparedStatement updateLoggedIn = dbConnection.prepareStatement(updateLoggedInQuery))
			{
				updateLoggedIn.setString(1, username);
				updateLoggedIn.executeUpdate();
				updateLoggedIn.close();
			} catch (SQLException e)
			{
				throw new LoginException("Unable to update " + username + "; SQLException when updating loggedin " +
					  "column.");
			}
		}
	}
}
