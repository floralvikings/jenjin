package com.jenjinstudios.jgsf;

import com.jenjinstudios.sql.SQLHandler;

import java.util.logging.Logger;

/**
 * A server that maintains a connection with a SQLandler.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class SqlEnabledServer<T extends ClientHandler> extends TaskedServer<T>
{

	/** The logger used by this class. */
	static final Logger LOGGER = Logger.getLogger(SqlEnabledServer.class.getName());
	/** The SQLHandler used by this SqlEnabledServer. */
	private SQLHandler sqlHandler;

	/**
	 * Construct a new SqlEnabledServer.
	 *
	 * @param ups          The cycles per second at which this server will run.
	 * @param port         The port number on which this server will listen.
	 * @param handlerClass The class of ClientHandler used by this SqlEnabledServer.
	 * @param sqlHandler   The SQLHandler to be used by this object.
	 */
	public SqlEnabledServer(int ups, int port, Class<? extends T> handlerClass, SQLHandler sqlHandler)
	{
		super(ups, port, handlerClass);
		this.sqlHandler = sqlHandler;
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

}
