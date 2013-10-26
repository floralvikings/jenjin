package com.jenjinstudios.jgsf;

import com.jenjinstudios.sql.SQLHandler;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * A server that maintains a connection with a SQLandler.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class SqlEnabledServer<T extends ClientHandler> extends Server<T>
{

	/** The logger used by this class. */
	static final Logger LOGGER = Logger.getLogger(SqlEnabledServer.class.getName());
	/** The SQLHandler used by this SqlEnabledServer. */
	private SQLHandler sqlHandler;
	/** flags whether the server has connected to the database. */
	private boolean connectedToDB;

	/**
	 * Construct a new SqlEnabledServer without a SQLHandler.
	 *
	 * @param ups          The cycles per second at which this server will run.
	 * @param port         The port number on which this server will listen.
	 * @param handlerClass The class of ClientHandler used by this SqlEnabledServer.
	 * @param sqlHandler   The SQLHandler to be used by this object.
	 *
	 * @throws java.sql.SQLException If there's a SQL exception.
	 */
	public SqlEnabledServer(int ups, int port, Class<? extends T> handlerClass, SQLHandler sqlHandler) throws SQLException
	{
		super(ups, port, handlerClass);
		setSQLHandler(sqlHandler);
	}

	/**
	 * Set the SQLHandler for this server.
	 *
	 * @param handler The SQLHandler to be used by this server
	 *
	 * @throws SQLException If the SQLHandler has already been set for this server.
	 */
	protected void setSQLHandler(SQLHandler handler) throws SQLException
	{
		if (sqlHandler != null) throw new SQLException("SQL Handler already set.");
		sqlHandler = handler;
		connectedToDB = true;
	}

	/**
	 * The SQLHandler used by this SqlEnabledServer.
	 *
	 * @return The SQLHandler used by this SqlEnabledServer.
	 */
	public SQLHandler getSqlHandler()
	{
		return sqlHandler;
	}

	/**
	 * flags whether the server has connected to the database.
	 *
	 * @return true if the server is connected to the databse.
	 */
	public boolean isConnectedToDB()
	{
		return connectedToDB;
	}
}
