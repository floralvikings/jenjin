package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.User;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ClientHandler} class is used to communicate with an individual client.
 *
 * @author Caleb Brinkman
 */
public class ClientHandler<T extends ServerMessageContext> extends Connection<T>
{
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    /** The server. */
	private final Server server;
	/** The id of the client handler. */
	private int handlerId = -1;

	/**
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary to
     * send the client a FirstConnectResponse message with the server's UPS
     *
     * @param s The server for which this handler works.
	 * @param messageStreamPair The MessageIO used to send and receive messages.
	 * @param context The context in which messages should be executed.
	 */
	public ClientHandler(Server s, MessageStreamPair messageStreamPair, T context) {
		super(messageStreamPair, context);
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
		if (getMessageContext().getUser() != null)
		{
            try
            {
				server.getAuthenticator().logOutUser(getMessageContext().getUser().getUsername());
			} catch (AuthenticationException e)
			{
                LOGGER.log(Level.WARNING, "Unable to perform emergency logout.", e);
            }
        }
        getServer().removeClient(this);
		super.shutdown();
	}

	/**
	 * The server.
     *
     * @return The server for which this client handler works.
     */
	public Server getServer() { return server; }

	/**
	 * Get the ClientHandler ID for this client handler.
     *
     * @return The ID of this client handler.
     */
    public int getHandlerId() { return handlerId; }

	/**
	 * Set the User associated with this ClientHandler.
     *
	 * @param user The User associated with this ClientHandler.
	 */
	public void setUser(User user) { }
}
