package com.jenjinstudios.chatserver;

import com.jenjinstudios.jgsf.Server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the JGSA as a Chat Server.
 *
 * @author Caleb Brinkman
 */
public class ChatServer extends Server
{
	/** The logger for this class. */
	static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());
	/** The updates-per-second for this server. */
	private static final int UPS = 10;
	/** The port on which this server listens. */
	private static final int PORT = 51019;

	/** Construct a new ChatServer. */
	public ChatServer()
	{
		super(UPS);
		addListener(PORT);
	}

	/**
	 * Add a new ChatListener to this server.
	 *
	 * @param port The port number on which to listen.
	 */
	@Override
	public void addListener(int port)
	{
		synchronized (clientListeners)
		{
			try
			{
				clientListeners.add(new ChatListener(this, port));
			} catch (Exception ex)
			{
				LOGGER.log(Level.SEVERE, "Error adding client", ex);
			}
		}
	}
}
