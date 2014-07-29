package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.message.ServerMessageFactory;
import com.jenjinstudios.server.sql.LoginException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ClientHandler} class is used to communicate with an individual client.
 * @author Caleb Brinkman
 */
public class ClientHandler extends Connection
{
	private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
	/** The server. */
	private final AuthServer<? extends ClientHandler> server;
	/** The message factory used by this ClientHandler. */
	private final ServerMessageFactory messageFactory;
	/** The id of the client handler. */
	private int handlerId = -1;
	/** The time at which this client was successfully logged in. */
	private long loggedInTime;
	/** Flags whether the connection acknowledgement response has been sent. */
	private boolean firstConnectResponseSent;
	private User user;

	/**
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler,
	 * it is necessary to
	 * send the client a FirstConnectResponse message with the server's UPS
	 * @param s The server for which this handler works.
	 */
	public ClientHandler(AuthServer<? extends ClientHandler> s, MessageIO messageIO) {
		super(messageIO);
		setName("ClientHandler with unset ID");
		server = s;

		this.messageFactory = new ServerMessageFactory(this);
	}

	/**
	 * Send a connection acknowledgement response.
	 */
	public void sendFirstConnectResponse() {
		if (!firstConnectResponseSent)
		{
			Message firstConnectResponse = getMessageFactory().generateFirstConnectResponse(getServer().UPS);
			queueOutgoingMessage(firstConnectResponse);
			firstConnectResponseSent = true;
		}
	}

	/**
	 * Set the id for this handler.
	 * @param id The new id number for the handler.
	 */
	public void setHandlerId(int id) {
		handlerId = id;
		super.setName("Client Handler " + handlerId);
	}

	/** Update anything that needs to be taken care of before sendAllMessages. */
	public void update() { }

	/** Shut down the client handler. */
	@Override
	public void shutdown() {
		if (getUser() != null)
		{
			try
			{
				server.getAuthenticator().logOutUser(user.getUsername());
			} catch (LoginException e)
			{
				LOGGER.log(Level.WARNING, "Unable to perform emergency logout.", e);
			}
		}
		getServer().removeClient(this);
	}

	@Override
	public ServerMessageFactory getMessageFactory() { return messageFactory; }

	/**
	 * The server.
	 * @return The server for which this client handler works.
	 */
	public AuthServer<? extends ClientHandler> getServer() { return server; }

	/**
	 * Queue a message indicating the success or failure of a logout attempt.
	 * @param success Whether the attempt was successful.
	 */
	public void sendLogoutStatus(boolean success) {
		Message logoutResponse = getMessageFactory().generateLogoutResponse(success);
		queueOutgoingMessage(logoutResponse);
	}

	/**
	 * Get the time at which this client was successfully logged in.
	 * @return The time at which this client was successfully logged in.
	 */
	public long getLoggedInTime() { return loggedInTime; }

	/**
	 * Queue a message indicating the success or failure of a login attempt.
	 */
	public void setLoggedInTime(long loggedInTime) { this.loggedInTime = loggedInTime; }

	/**
	 * Get the ClientHandler ID for this client handler.
	 * @return The ID of this client handler.
	 */
	public int getHandlerId() { return handlerId; }

	public User getUser() { return user; }

	public void setUser(User user) { this.user = user; }
}
