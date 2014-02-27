package com.jenjinstudios.net;

import com.jenjinstudios.sql.SQLHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * A Server with access to a SqlHandler and MySql database.
 * @author Caleb Brinkman
 */
public class AuthServer<T extends ClientHandler> extends TaskedServer<T>
{
	/** The SQLHandler used by this Server. */
	private SQLHandler sqlHandler;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @param ups The cycles per second at which this server will run.
	 * @param port The port number on which this server will listen.
	 * @param handlerClass The class of ClientHandler used by this Server.
	 * @param sqlHandler The SqlHandler responsible for communicating with a MySql database.
	 * @throws java.io.IOException If there is an IO Error when initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler constructor.
	 * @throws javax.xml.parsers.ParserConfigurationException If there is an error parsing XML files.
	 * @throws org.xml.sax.SAXException If there is an error parsing XML files.
	 */
	public AuthServer(int ups, int port, Class<? extends T> handlerClass, SQLHandler sqlHandler) throws IOException, NoSuchMethodException, ParserConfigurationException, SAXException {
		super(ups, port, handlerClass);
		this.sqlHandler = sqlHandler;
	}

	/**
	 * The SQLHandler used by this Server.
	 * @return The SQLHandler used by this Server.
	 */
	public SQLHandler getSqlHandler() {
		return sqlHandler;
	}
}
