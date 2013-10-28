package com.jenjinstudios.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The superclass for any handlers that need to connect to a sql database.
 *
 * @author Caleb Brinkman
 */
public class SQLConnector
{
	/** The String used in connection protocol. */
	private static final String connectionStringProtocol = "jdbc:mysql:thin://";
	/** The Logger used for this class. */
	private static final Logger LOGGER = Logger.getLogger(SQLConnector.class.getName());
	/** The name of the database used by this server. */
	protected final String dbName;
	/** The username used to access the database. */
	private final String dbUsername;
	/** The password used to access the database. */
	private final String dbPassword;
	/** The url used to connect with the SQL database. */
	private final String dbUrl;
	/** The string used to get all information about the user. */
	private final String userQuery;
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
	 *
	 * @throws java.sql.SQLException If there is an issue connecting to the database.
	 */
	public SQLConnector(String dbAddress, String dbName, String dbUsername, String dbPassword) throws SQLException
	{
		/* The address of the database to which to connect. */
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.dbName = dbName;
		dbUrl = connectionStringProtocol + dbAddress + "/" + dbName;
		try
		{
			Class.forName("org.drizzle.jdbc.DrizzleDriver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to register Drizzle driver!");
		}
		userQuery = "SELECT * FROM " + dbName + ".users WHERE username = ?";

		connectToDatabase();
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
	 * Get the query string used to pull user information.
	 *
	 * @return The query string used to pull user information.
	 */
	public String getUserQuery()
	{
		return userQuery;
	}

	/**
	 * Get the Connection used to connect to the sql database.
	 *
	 * @return The Connection used to connect to the sql database.
	 */
	protected Connection getDbConnection()
	{
		return dbConnection;
	}
}
