package com.jenjinstudios.jgsf;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The SQLHandler class is responsible for connecting to and querying the SQL database associated with a given Server.
 *
 * @author Caleb Brinkman
 */
public class SQLHandler
{
	/** The Logger used for this class. */
	public static final Logger LOGGER = Logger.getLogger(SQLHandler.class.getName());
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
		String loggedInQuery;
		loggedInQuery = "SELECT " + loggedInColumn + ",username FROM " + dbName + ".users WHERE username = ? AND " +
				"password = ?";
		try
		{
			// Check to see if the user is already logged in.
			PreparedStatement loggedInCheck;
			loggedInCheck = dbConnection.prepareStatement(loggedInQuery,
					ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			loggedInCheck.setString(1, username);
			loggedInCheck.setString(2, password);
			ResultSet loggedInResults = loggedInCheck.executeQuery();
			loggedInResults.next();
			boolean isLoggedIn = loggedInResults.getBoolean(loggedInColumn);
			if (isLoggedIn)
			{
				loggedInCheck.close();
				return success;
			}
			String updateLoggedInQuery = "UPDATE " + dbName + ".users SET " + loggedInColumn + "=1 WHERE " +
					"username = ? AND password = ?";
			PreparedStatement loggedInUpdate;
			loggedInUpdate = dbConnection.prepareStatement(updateLoggedInQuery);
			loggedInUpdate.setString(1, username);
			loggedInUpdate.setString(2, password);
			loggedInUpdate.executeUpdate();
			loggedInCheck.close();
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
		String loggedInQuery;
		loggedInQuery = "SELECT " + loggedInColumn + ",username FROM " + dbName + ".users WHERE username = ?";
		try
		{
			// Check to see if the user is already logged in.
			PreparedStatement loggedInCheck = dbConnection.prepareStatement(loggedInQuery,
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			loggedInCheck.setString(1, username);
			ResultSet loggedInResults = loggedInCheck.executeQuery();
			loggedInResults.next();
			boolean isLoggedIn = loggedInResults.getBoolean(loggedInColumn);
			if (!isLoggedIn)
			{
				loggedInCheck.close();
				return success;
			}
			String updateLoggedInQuery = "UPDATE " + dbName + ".users SET " + loggedInColumn + "=0 WHERE " +
					"username = ?";
			PreparedStatement loggedInUpdate;
			loggedInUpdate = dbConnection.prepareStatement(updateLoggedInQuery);
			loggedInUpdate.setString(1, username);
			loggedInUpdate.executeUpdate();
			loggedInCheck.close();
			success = true;
		} catch (SQLException e)
		{
			LOGGER.log(Level.INFO, "Failed to log out user: {0}", username);
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
}
