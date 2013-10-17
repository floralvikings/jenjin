package com.jenjinstudios.jgsf;

import com.jenjinstudios.jgsf.message.ServerExecutableMessage;
import com.jenjinstudios.message.ExecutableMessage;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.net.Communicator;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/**
 * The {@code ClientHandler} class is used to communicate with an individual client.
 *
 * @author Caleb Brinkman
 */
public class ClientHandler extends Communicator
{
	/** The list of messages to be sendAllMessages after the world update. */
	private final LinkedList<Message> outgoingMessages;
	/** The server. */
	private final Server<? extends ClientHandler> server;
	/** Flags whether the socket is connected. */
	private boolean connected;
	/** The id of the client handler. */
	private int handlerId = -1;
	/** Flags whether the user is logged in. */
	private boolean loggedIn;
	/** The username of this client. */
	private String username;
	/** The time at which this client was successfully logged in. */
	private long loggedInTime;


	/**
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary to
	 * send the client a FirstConnectResponse message with the server's UPS
	 *
	 * @param s  The server for which this handler works.
	 * @param sk The socket used to communicate with the client.
	 *
	 * @throws IOException If the socket is unable to connect.
	 */
	public ClientHandler(Server<? extends ClientHandler> s, Socket sk) throws IOException
	{
		setName("ClientHandler: " + sk.getInetAddress());
		server = s;
		super.setSocket(sk);
		outgoingMessages = new LinkedList<>();

		connected = true;

		Message firstConnectResponse = new Message("FirstConnectResponse");
		firstConnectResponse.setArgument("ups", server.UPS);
		queueMessage(firstConnectResponse);
	}

	/**
	 * Add a message to the sendAllMessages queue, to be sent at the next sendAllMessages.
	 *
	 * @param o The object (message) to be sent to the client.
	 */
	public void queueMessage(Message o)
	{
		synchronized (outgoingMessages)
		{
			outgoingMessages.add(o);
		}
	}

	/**
	 * Set the id for this handler.
	 *
	 * @param id The new id number for the handler.
	 */
	public void setID(int id)
	{
		handlerId = id;
		super.setName("Client Handler " + handlerId);
	}

	/** Update anything that needs to be taken care of before sendAllMessages. */
	@SuppressWarnings("EmptyMethod")
	public void update()
	{
	}

	/** Reset anything that needs to be taken care of after sendAllMessages. */
	@SuppressWarnings("EmptyMethod")
	public void refresh()
	{
	}

	/** Send all messages in the message queue to the client. */
	public void sendAllMessages()
	{
		synchronized (outgoingMessages)
		{
			while (!outgoingMessages.isEmpty())
			{
				writeMessage(outgoingMessages.remove());
			}
		}
	}

	/**
	 * Send the specified message to the client.
	 *
	 * @param o The message to send to the client.
	 */
	public void writeMessage(Message o)
	{
		try
		{
			getOutputStream().writeMessage(o);
		} catch (Exception ex)
		{
			shutdown();
		}
	}

	/** Shut down the client handler. */
	public void shutdown()
	{
		// Try and log out if not already.  This is an "emergency" logout because the connection closed without a
		// proper logout, so we handle the query directly instead of in an executable message.
		// This is a big no-no, but this can be caused by an unexpected server or client shutdown, which means that
		// there may not be time to finish any executable messages created.  I'm not happy about it but there it is.
		// TODO Make this better.
		if (isLoggedIn())
			loggedIn = !server.getSqlHandler().logOutUser(username);
		closeLink();
		getServer().removeClient(this);
	}

	/**
	 * Flags whether the user is logged in.
	 *
	 * @return true if the user is logged in.
	 */
	public boolean isLoggedIn()
	{
		return loggedIn;
	}

	/**
	 * The server.
	 *
	 * @return The server for which this client handler works.
	 */
	public Server<? extends ClientHandler> getServer()
	{
		return server;
	}

	/** Close the link with the client, if possible. */
	protected void closeLink()
	{
		if (connected)
		{
			super.closeLink();
		}
		connected = false;
	}

	/**
	 * Queue a message indicating the success or failure of a login attempt.
	 *
	 * @param success Whether the attempt was successful.
	 */
	public void setLoginStatus(boolean success)
	{
		loggedIn = success;
		loggedInTime = server.getCycleStartTime();

	}

	/**
	 * Queue a message indicating the success or failure of a logout attempt.
	 *
	 * @param success Whether the attempt was successful.
	 */
	public void sendLogoutStatus(boolean success)
	{
		loggedIn = !success;
		Message logoutResponse = new Message("LogoutResponse");
		logoutResponse.setArgument("success", success);
		queueMessage(logoutResponse);
	}

	/**
	 * Get an executable message for a given message.
	 *
	 * @param message The message to be used.
	 *
	 * @return The ExecutableMessage.
	 */
	@Override
	protected ExecutableMessage getExecutableMessage(Message message)
	{
		return ServerExecutableMessage.getServerExecutableMessageFor(this, message);
	}

	/**
	 * The username of this client.
	 *
	 * @return The username of this client.
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Set the username of this client handler.
	 *
	 * @param username The client handler's username.
	 */
	public void setUsername(String username)
	{
		this.username = username;
		server.clientUsernameSet(username, this);
	}

	/**
	 * Get the time at which this client was successfully logged in.
	 *
	 * @return The time at which this client was successfully logged in.
	 */
	public long getLoggedInTime()
	{
		return loggedInTime;
	}

	/**
	 * Get the ClientHandler ID for this client handler.
	 *
	 * @return The ID of this client handler.
	 */
	public int getHandlerId()
	{
		return handlerId;
	}

	/**
	 * Immediately force send a message. This method should only be used if a message is <i>extremely</i> time dependent,
	 * otherwise messages should be queued using the {@code queueMessage} method, because this method may cause
	 * synchronization issues.
	 *
	 * @param message The message to send.
	 *
	 * @throws IOException If there is an IOException.
	 */
	public void forceMessage(Message message) throws IOException
	{
		getOutputStream().writeMessage(message);
	}
}
