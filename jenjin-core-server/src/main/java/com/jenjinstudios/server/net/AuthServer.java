package com.jenjinstudios.server.net;

import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.sql.SQLConnector;

import java.io.IOException;

/**
 * A Server with access to a SqlHandler and MySql database.
 * @author Caleb Brinkman
 */
public class AuthServer<T extends ClientHandler> extends TaskedServer<T>
{
	/** The SQLHandler used by this Server. */
	private SQLConnector sqlConnector;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @param ups The cycles per second at which this server will run.
	 * @param port The port number on which this server will listen.
	 * @param handlerClass The class of ClientHandler used by this Server.
	 * @param sqlConnector The SqlHandler responsible for communicating with a MySql database.
	 * @throws java.io.IOException If there is an IO Error when initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler
	 * constructor.
	 */
	public AuthServer(MessageRegistry mr, int ups, int port, Class<? extends T> handlerClass, SQLConnector sqlConnector) throws IOException, NoSuchMethodException {
		super(mr, ups, port, handlerClass);
		this.sqlConnector = sqlConnector;
	}

	/**
	 * The SQLHandler used by this Server.
	 * @return The SQLHandler used by this Server.
	 */
	public SQLConnector getSqlConnector() {
		return sqlConnector;
	}
}
