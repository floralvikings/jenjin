package com.jenjinstudios.net;

import com.jenjinstudios.io.ExecutableMessage;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.message.ServerExecutableMessage;
import com.jenjinstudios.message.ServerMessageFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * The {@code ClientHandler} class is used to communicate with an individual client.
 * @author Caleb Brinkman
 */
public class ClientHandler extends Connection
{
	/** The server. */
	private final AuthServer<? extends ClientHandler> server;
	/** The message factory used by this ClientHandler. */
	private final ServerMessageFactory messageFactory;
	/** The id of the client handler. */
	private int handlerId = -1;
	/** Flags whether the user is logged in. */
	private boolean loggedIn;
	/** The username of this client. */
	private String username;
	/** The time at which this client was successfully logged in. */
	private long loggedInTime;
	/** Flags whether the connection acknowledgement response has been sent. */
	private boolean firstConnectResponseSent;


	/**
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary to
	 * send the client a FirstConnectResponse message with the server's UPS
	 * @param s The server for which this handler works.
	 * @param sk The socket used to communicate with the client.
	 * @param messageRegistry The MessageRegistry for this ClientHandler.
	 * @throws IOException If the socket is unable to connect.
	 */
	public ClientHandler(AuthServer<? extends ClientHandler> s, Socket sk, MessageRegistry messageRegistry) throws IOException {
		setName("ClientHandler: " + sk.getInetAddress());
		setMessageRegistry(messageRegistry);
		server = s;
		super.setSocket(sk);

		this.messageFactory = new ServerMessageFactory(this);
	}

	/**
	 * Send a connection acknowledgement response.
	 */
	public void sendFirstConnectResponse() {
		if (firstConnectResponseSent) return;
		Message firstConnectResponse = getMessageFactory().generateFirstConnectResponse(getServer().UPS);
		queueMessage(firstConnectResponse);
		firstConnectResponseSent = true;
	}

	/**
	 * Set the id for this handler.
	 * @param id The new id number for the handler.
	 */
	public void setID(int id) {
		handlerId = id;
		super.setName("Client Handler " + handlerId);
	}

	/** Update anything that needs to be taken care of before sendAllMessages. */
	public void update() {
		for (Runnable r : getSyncedTasks())
		{
			server.addSyncedTask(r);
		}
	}

	/** Reset anything that needs to be taken care of after sendAllMessages. */
	@SuppressWarnings("EmptyMethod")
	public void refresh() {
	}

	/** Shut down the client handler. */
	public void shutdown() {
		// Try and log out if not already.  This is an "emergency" logout because the connection closed without a
		// proper logout, so we handle the query directly instead of in an executable message.
		if (isLoggedIn())
			loggedIn = !server.getSqlHandler().logOutUser(username);
		closeLink();
		getServer().removeClient(this);
	}

	/**
	 * Get an executable message for a given message.
	 * @param message The message to be used.
	 * @return The ExecutableMessage.
	 */
	@Override
	protected ExecutableMessage getExecutableMessage(Message message) {
		return ServerExecutableMessage.getServerExecutableMessageFor(this, message);
	}

	/**
	 * The server.
	 * @return The server for which this client handler works.
	 */
	public AuthServer<? extends ClientHandler> getServer() {
		return server;
	}

	/**
	 * Flags whether the user is logged in.
	 * @return true if the user is logged in.
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * Queue a message indicating the success or failure of a login attempt.
	 * @param success Whether the attempt was successful.
	 */
	public void setLoginStatus(boolean success) {
		loggedIn = success;
		loggedInTime = server.getCycleStartTime();

	}

	/**
	 * Queue a message indicating the success or failure of a logout attempt.
	 * @param success Whether the attempt was successful.
	 */
	public void sendLogoutStatus(boolean success) {
		loggedIn = !success;
		Message logoutResponse = getMessageFactory().generateLogoutResponse(success);
		queueMessage(logoutResponse);
	}

	/**
	 * The username of this client.
	 * @return The username of this client.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set the username of this client handler.
	 * @param username The client handler's username.
	 */
	public void setUsername(String username) {
		this.username = username;
		server.clientUsernameSet(username, this);
	}

	/**
	 * Get the time at which this client was successfully logged in.
	 * @return The time at which this client was successfully logged in.
	 */
	public long getLoggedInTime() {
		return loggedInTime;
	}

	/**
	 * Get the ClientHandler ID for this client handler.
	 * @return The ID of this client handler.
	 */
	public int getHandlerId() {
		return handlerId;
	}

	/**
	 * Immediately force send a message. This method should only be used if a message is <i>extremely</i> time
	 * dependent, otherwise messages should be queued using the {@code queueMessage} method, because this method may
	 * cause synchronization issues.
	 * @param message The message to send.
	 * @throws IOException If there is an IOException.
	 */
	public void forceMessage(Message message) throws IOException {
		getOutputStream().writeMessage(message);
	}

	@Override
	public ServerMessageFactory getMessageFactory() { return messageFactory; }
}
