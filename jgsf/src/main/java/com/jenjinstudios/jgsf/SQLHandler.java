package com.jenjinstudios.jgsf;

import com.jenjinstudios.util.Hasher;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;

/**
 * The SQLHandler class is responsible for connecting to and querying the SQL database associated with a given Server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class SQLHandler
{
	/** The Logger used for this class. */
	private static final Logger LOGGER = Logger.getLogger(SQLHandler.class.getName());
	/** The String used in connection protocol. */
	private static final String connectionStringProtocol = "jdbc:mysql:thin://";
	/** The username used to access the database. */
	private final String dbUsername;
	/** The password used to access the database. */
	private final String dbPassword;
	/** The name of the database used by this server. */
	private final String dbName;
	/** The url used to connect with the SQL database. */
	private final String dbUrl;
	/** The name of the column in the user table specifying whether the user is currently logged in. */
	private final String loggedInColumn;
	/** Flags whether this SQLHandler is connected to the database. */
	private boolean connected;
	/** The connection used to communicate with the SQL database. */
	private Connection dbConnection;
	/** The string used to get all information about the user. */
	private final String USER_QUERY;

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 *
	 * @param dbAddress  The URL of the database, in the format www.example.place
	 * @param dbName     The name of the database.
	 * @param dbUsername The username used to access the database.
	 * @param dbPassword The password used to access the database
	 * @throws SQLException If there is an issue connecting to the database.
	 */
	public SQLHandler(String dbAddress, String dbName, String dbUsername, String dbPassword) throws SQLException
	{
		/* The address of the database to which to connect. */
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.dbName = dbName;
		dbUrl = connectionStringProtocol + dbAddress + "/" + dbName;
		loggedInColumn = "loggedin";
		try
		{
			Class.forName("org.drizzle.jdbc.DrizzleDriver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to register Drizzle driver!");
		}
		USER_QUERY = "SELECT username, loggedin, salt, password FROM " + dbName + ".users WHERE username = ?";

		connectToDatabase();
	}

	/**
	 * Attempt to log the given user with the given password into the database.  This method does not perform any sort
	 * of hashing or encryption on the password.  If the user is already logged in this method will return false.
	 * <p/>
	 * This method should be overwritten by implementations, or called from super if they still wish to use the
	 * "loggedIn" column.
	 *
	 * @param username The username of the user to be logged in.
	 * @param password The password of the user to be logged in.
	 * @return true if the user was logged in successfully, false if the user was already logged in or the update
	 *         to the database failed.
	 */
	public synchronized boolean logInUser(String username, String password)
	{
		boolean success = false;
		if (!connected)
			return success;
		try
		{
			ResultSet results = makeUserQuery(username);
			results.next();
			// Determine if the user is logged in.  If yes, end of method.
			boolean loggedIn = results.getBoolean(loggedInColumn);
			if (loggedIn)
				return success;
			// Hash the user-supplied password with the salt in the database.
			String hashedPassword = Hasher.getHashedString(password, results.getString("salt"));
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
	 * Attempt to log out the given user with the given password into the database.  This method does not perform any
	 * sort
	 * of hashing or encryption on the password.  If the user is already logged in this method will return false.
	 *
	 * @param username The username of the user to be logged out.
	 * @return true if the user was logged out successfully, false if the user was already logged out or the update
	 *         to the database failed.
	 */
	public synchronized boolean logOutUser(String username)
	{
		boolean success = false;
		if (!connected)
			return success;
		try
		{
			ResultSet results = makeUserQuery(username);
			results.next();
			// Determine if the user is logged in.  If no, end of method.
			boolean loggedIn = results.getBoolean(loggedInColumn);
			results.getStatement().close();
			if (!loggedIn)
				return success;

			updateLoggedinColumn(username, false);
			success = true;
		} catch (SQLException | IndexOutOfBoundsException e)
		{
			LOGGER.log(Level.FINE, "Failed to log in user: {0}", username);
			success = false;
		}
		return success;
	}

	/**
	 * Attempt to connect to the database.
	 *
	 * @throws SQLException If there is an error connecting to the SQL database.
	 */
	private void connectToDatabase() throws SQLException
	{
		dbConnection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		connected = true;
	}

	/**
	 * Get whether this SQLHandler is connected to the database.
	 *
	 * @return true if the SQLHandler has successfully connected to the database.
	 */
	public boolean isConnected()
	{
		return connected;
	}

	/**
	 * Query the database for user info.
	 *
	 * @param username The username of the user we're looking for.
	 * @return The ResultSet returned by the query.
	 * @throws SQLException If there is a SQL error.
	 */
	private ResultSet makeUserQuery(String username) throws SQLException
	{
		PreparedStatement statement = dbConnection.prepareStatement(USER_QUERY, TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE);
		statement.setString(1, username);
		return statement.executeQuery();
	}


	/**
	 * Update the loggedin column to reflect the supplied boolean.
	 *
	 * @param username The user being queried.
	 * @param status   The new status of the loggedin column.
	 * @throws SQLException If there is a SQL error.
	 */
	private void updateLoggedinColumn(String username, boolean status) throws SQLException
	{
		String newValue = status ? "1" : "0";
		String updateLoggedInQuery = "UPDATE " + dbName + ".users SET " + loggedInColumn + "=" + newValue + " WHERE " +
				"username = ?";
		PreparedStatement updateLoggedin;
		updateLoggedin = dbConnection.prepareStatement(updateLoggedInQuery);
		updateLoggedin.setString(1, username);
		updateLoggedin.executeUpdate();
		updateLoggedin.close();
	}
}
