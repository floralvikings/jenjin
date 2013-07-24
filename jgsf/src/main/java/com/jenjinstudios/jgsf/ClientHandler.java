package com.jenjinstudios.jgsf;

import com.jenjinstudios.io.MessageInputStream;
import com.jenjinstudios.io.MessageOutputStream;
import com.jenjinstudios.jgcf.Client;
import com.jenjinstudios.jgcf.message.BaseMessage;
import com.jenjinstudios.jgcf.message.ExecutableMessage;
import com.jenjinstudios.jgsf.message.ServerExecutableMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ClientHandler} class is used to communicate with an individual client.
 *
 * @author Caleb Brinkman
 */
public class ClientHandler extends Thread
{
	/** The Socket the handler uses to communicate. */
	private final Socket sock;
	/** The list of messages to be broadcast after the world update. */
	private final LinkedList<BaseMessage> broadcastMessages;
	/** Flags whether the socket is connected. */
	private boolean linkOpen;
	/** The server. */
	private final Server<? extends ClientHandler> server;
	/** The id of the client handler. */
	private int handlerId = -1;
	/** Flags whether the user is logged in. */
	private boolean loggedIn;
	/** The username of this client. */
	private String username;
	/** The input stream used for reading from the client. */
	private MessageInputStream inputStream;
	/** The output stream used for writing to the client. */
	private MessageOutputStream outputStream;
	/** The time at which this client was successfully logged in. */
	private long loggedInTime;


	/**
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary
	 * to send the client a FirstConnectResponse message with the server's UPS
	 *
	 * @param s  The server for which this handler works.
	 * @param sk The socket used to communicate with the client.
	 * @throws IOException If the socket is unable to connect.
	 */
	public ClientHandler(Server<? extends ClientHandler> s, Socket sk) throws IOException
	{
		super("ClientHandler: " + sk.getInetAddress());
		server = s;
		sock = sk;
		broadcastMessages = new LinkedList<>();
		inputStream = new MessageInputStream(sock.getInputStream());
		outputStream = new MessageOutputStream(sock.getOutputStream());
		linkOpen = true;
		queueMessage(new BaseMessage(Client.FIRST_CONNECT_ID, server.UPS));
	}

	/**
	 * Add a message to the broadcast queue, to be sent at the next broadcast.
	 *
	 * @param o The object (message) to be sent to the client.
	 */
	public void queueMessage(BaseMessage o)
	{
		synchronized (broadcastMessages)
		{
			broadcastMessages.add(o);
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

	/** Update anything that needs to be taken care of before broadcast. */
	@SuppressWarnings("EmptyMethod")
	public void update()
	{
	}

	/** Reset anything that needs to be taken care of after broadcast. */
	@SuppressWarnings("EmptyMethod")
	public void refresh()
	{
	}

	/** Send all messages in the message queue to the client. */
	public void broadcast()
	{
		synchronized (broadcastMessages)
		{
			while (!broadcastMessages.isEmpty())
			{
				sendMessage(broadcastMessages.remove());
			}
		}
	}

	/**
	 * Send the specified message to the client.
	 *
	 * @param o The message to send to the client.
	 */
	private void sendMessage(BaseMessage o)
	{
		try
		{
			outputStream.writeMessage(o);
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
	final void closeLink()
	{
		if (linkOpen)
		{
			try
			{
				inputStream.close();
				outputStream.close();
				sock.close();
			} catch (IOException ex)
			{
				Logger.getLogger(ClientHandler.class.getName()).log(Level.WARNING, "Error closing"
						+ "link with client.", ex);
			}
		}
		linkOpen = false;
	}

	/**
	 * Queue a message indicating the success or failure of a login attempt.
	 *
	 * @param success Whether the attempt was successful.
	 */
	public void sendLoginStatus(boolean success)
	{
		loggedIn = success;
		loggedInTime = server.getCycleStartTime();
		queueMessage(new BaseMessage(Client.LOGIN_RESP_ID, success, loggedInTime));
	}

	/**
	 * Queue a message indicating the success or failure of a logout attempt.
	 *
	 * @param success Whether the attempt was successful.
	 */
	public void sendLogoutStatus(boolean success)
	{
		loggedIn = !success;
		queueMessage(new BaseMessage(Client.LOGOUT_RESP_ID, success));
	}

	/** Enter a loop that receives and processes messages until the link is closed. */
	@Override
	public void run()
	{
		Server.LOGGER.log(Level.FINE, "Client Handler Started. Link open:{0}", linkOpen);
		BaseMessage message;
		while (linkOpen)
		{
			try
			{
				message = inputStream.readMessage();
				if (message == null)
				{
					Server.LOGGER.log(Level.FINE, "Received null message, shutting down ClientHandler");
					shutdown();
					break;
				}
				Server.LOGGER.log(Level.FINE, "Message received: {0}", message);
				processMessage(message);
			} catch (Exception ex)
			{
				Server.LOGGER.log(Level.SEVERE, "Exception with client handler.", ex);
				shutdown();
			}
		}
	}

	/**
	 * Process the given message.
	 * @param message The message to be processed.
	 */
	private void processMessage(BaseMessage message)
	{
		ExecutableMessage exec;
		exec = ServerExecutableMessage.getServerExecutableMessageFor(this, message);
		if (exec != null)
		{
			exec.runASync();
			getServer().addSyncedTask(exec);
		} else
		{
			this.shutdown();
		}
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
}
