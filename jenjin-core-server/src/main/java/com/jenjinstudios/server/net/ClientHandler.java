package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.message.ServerMessageFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ClientHandler} class is used to communicate with an individual client.
 *
 * @author Caleb Brinkman
 */
public class ClientHandler extends Connection
{
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    /** The server. */
	private final Server server;
	/** The id of the client handler. */
	private int handlerId = -1;
    /** The time at which this client was successfully logged in. */
    private long loggedInTime;
	private User user;

    /**
     * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary to
     * send the client a FirstConnectResponse message with the server's UPS
     *
     * @param s The server for which this handler works.
     * @param messageIO The MessageIO used to send and receive messages.
     */
	public ClientHandler(Server s, MessageIO messageIO) {
		super(messageIO);
        setName("ClientHandler with unset ID");
        server = s;
    }

    /**
     * Set the id for this handler.
     *
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
			} catch (AuthenticationException e)
			{
                LOGGER.log(Level.WARNING, "Unable to perform emergency logout.", e);
            }
        }
        getServer().removeClient(this);
    }

	/**
	 * The server.
     *
     * @return The server for which this client handler works.
     */
	public Server getServer() { return server; }

    /**
     * Queue a message indicating the success or failure of a logout attempt.
     *
     * @param success Whether the attempt was successful.
     */
    public void sendLogoutStatus(boolean success) {
		Message logoutResponse = ServerMessageFactory.generateLogoutResponse(success);
		getMessageIO().queueOutgoingMessage(logoutResponse);
    }

    /**
     * Get the time at which this client was successfully logged in.
     *
     * @return The time at which this client was successfully logged in.
     */
    public long getLoggedInTime() { return loggedInTime; }

    /**
     * Queue a message indicating the success or failure of a login attempt.
     *
     * @param loggedInTime The time at which this client handler has been authenticated.
     */
    public void setLoggedInTime(long loggedInTime) { this.loggedInTime = loggedInTime; }

    /**
     * Get the ClientHandler ID for this client handler.
     *
     * @return The ID of this client handler.
     */
    public int getHandlerId() { return handlerId; }

    /**
     * Get the User associated with this ClientHandler.
     *
     * @return The User associated with this ClientHandler.
     */
	public User getUser() { return user; }

    /**
     * Set the User associated with this ClientHandler.
     *
	 * @param user The User associated with this ClientHandler.
	 */
	public void setUser(User user) { this.user = user; }
}
