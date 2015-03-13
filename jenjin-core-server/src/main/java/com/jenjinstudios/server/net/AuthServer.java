package com.jenjinstudios.server.net;

import com.jenjinstudios.server.authentication.AbstractAuthenticator;

import java.io.IOException;

/**
 * A Server with access to a SqlHandler and MySql database.
 * @author Caleb Brinkman
 */
public class AuthServer extends TaskedServer
{
	/** The SQLHandler used by this Server. */
	private AbstractAuthenticator authenticator;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @param authenticator The SqlHandler responsible for communicating with a MySql database.
	 * @throws java.io.IOException If there is an IO Error when initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler
	 * constructor.
	 */
	public AuthServer(ServerInit initInfo, AbstractAuthenticator authenticator) throws IOException,
		  NoSuchMethodException {
		super(initInfo);
		this.authenticator = authenticator;
	}

	/**
	 * The SQLHandler used by this Server.
	 * @return The SQLHandler used by this Server.
	 */
	public AbstractAuthenticator getAuthenticator() { return authenticator; }
}
