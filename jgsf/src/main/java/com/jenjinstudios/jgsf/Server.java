package com.jenjinstudios.jgsf;

import com.jenjinstudios.message.MessageRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base SqlEnabledServer class for implementation of the JGSA.  It contains extensible execution functionality
 * designed to be used by Executable Messages from ClientHandlers.
 *
 * @author Caleb Brinkman
 */
public class Server<T extends ClientHandler> extends Thread
{
	/** The logger used by this class. */
	static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	/** The maximum number of allowed clients per server. */
	public static final int MAX_CLIENTS = 1000;
	/** The updates per second. */
	public final int UPS;
	/** The period of the update in milliseconds. */
	public final int PERIOD;
	/** The port on which this server will run. */
	private final int PORT;
	/** The list of {@code ClientListener}s working for this server. */
	private final LinkedList<ClientListener<T>> clientListeners;
	/** The list of {@code ClientHandler}s working for this server. */
	private final ArrayList<T> clientHandlers;
	/** The map of clients stored by username. */
	private final TreeMap<String, T> clientsByUsername;
	/** The class for ClientHandlers. */
	private final Class<? extends T> handlerClass;
	/** The current number of connected clients. */
	private int numClients;
	/** Indicates whether this server is initialized. */
	private volatile boolean initialized;

	/**
	 * Construct a new SqlEnabledServer without a SQLHandler.
	 *
	 * @param ups          The cycles per second at which this server will run.
	 * @param port         The port number on which this server will listen.
	 * @param handlerClass The class of ClientHandler used by this SqlEnabledServer.
	 */
	public Server(int ups, int port, Class<? extends T> handlerClass)
	{
		super("Server");
		LOGGER.log(Level.FINE, "Initializing SqlEnabledServer.");
		UPS = ups;
		PORT = port;
		PERIOD = 1000 / ups;
		this.handlerClass = handlerClass;
		clientsByUsername = new TreeMap<>();
		clientListeners = new LinkedList<>();
		clientHandlers = new ArrayList<>();
		for (int i = 0; i < MAX_CLIENTS; i++)
			clientHandlers.add(null);
		numClients = 0;
		MessageRegistry.registerXmlMessages();
		addListener();
	}

	/** Start a new Client Listener on the specified port. */
	@SuppressWarnings("unchecked")
	void addListener()
	{
		try
		{
			clientListeners.add((ClientListener<T>) new ClientListener<>(this, PORT, handlerClass));
		} catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Error adding client listener", e);
		}
	}

	/**
	 * Schedule a client to be removed during the next update.
	 *
	 * @param handler The client handler to be removed.
	 */
	void removeClient(ClientHandler handler)
	{
		synchronized (clientHandlers)
		{
			String username = handler.getUsername();
			if (username != null)
				clientsByUsername.remove(username);
			clientHandlers.set(handler.getHandlerId(), null);
			numClients--;
		}
	}

	/**
	 * Add new clients that have connected to the client listeners.
	 *
	 * @return true if new clients were added.
	 */
	public boolean getNewClients()
	{
		boolean clientsAdded = false;
		for (ClientListener<T> l : clientListeners)
		{
			LinkedList<T> nc = l.getNewClients();
			clientsAdded = !nc.isEmpty();
			for (T h : nc)
			{
				int nullIndex = clientHandlers.indexOf(null);
				clientHandlers.set(nullIndex, h);
				h.setID(nullIndex);
				h.start();
				numClients++;
			}
		}
		return clientsAdded;
	}

	/**
	 * Get the list of client handlers.
	 *
	 * @return The list of client handlers.
	 */
	public ArrayList<T> getClientHandlers()
	{
		return clientHandlers;
	}

	/**
	 * Broadcast all outgoing messages to clients.
	 *
	 * @throws java.io.IOException If there's an IO exception.
	 */
	public void broadcast() throws IOException
	{
		synchronized (clientHandlers)
		{
			for (ClientHandler current : clientHandlers)
			{
				if (current != null)
					current.sendAllMessages();
			}
		}
	}

	/** Update all clients before they sendAllMessages. */
	public void update()
	{
		synchronized (clientHandlers)
		{
			for (ClientHandler current : clientHandlers)
			{
				if (current != null)
				{
					current.update();
				}
			}
		}
	}

	/** Refresh all clients after they sendAllMessages. */
	public void refresh()
	{
		synchronized (clientHandlers)
		{
			for (ClientHandler current : clientHandlers)
			{
				if (current != null)
					current.refresh();
			}
		}
	}

	/** Run the server. */
	@Override
	public void run()
	{
		if (clientListeners.isEmpty())
		{
			Logger.getLogger(SqlEnabledServer.class.getName()).log(Level.INFO, "Executing server without "
					+ "any active client listeners.");
		}
		for (ClientListener<T> listener : clientListeners)
		{
			listener.listen();
		}

		initialized = true;
	}

	/** Start the server, and do not return until it is fully initialized. */
	public final void blockingStart()
	{
		start();
		while (!initialized) try
		{
			Thread.sleep(1);
		} catch (InterruptedException e)
		{
			LOGGER.log(Level.WARNING, "Issue with server blockingStart", e);
		}
	}

	/**
	 * Shutdown the server, forcing all client links to close.
	 *
	 * @throws IOException if there is an error shutting down a client.
	 */
	public void shutdown() throws IOException
	{
		synchronized (clientHandlers)
		{
			for (ClientHandler h : clientHandlers)
			{
				if (h != null)
					h.shutdown();
			}
		}
		synchronized (clientListeners)
		{
			for (ClientListener<T> l : clientListeners)
			{
				l.stopListening();
			}
		}

	}

	/**
	 * Return whether this server is initialized.
	 *
	 * @return true if the server has been initialized.
	 */
	public boolean isInitialized()
	{
		return initialized;
	}

	/**
	 * Get the ClientHandler with the given username.
	 *
	 * @param username The username of the client to look up.
	 *
	 * @return The client with the username specified; null if there is no client with this username.
	 */
	public T getClientHandlerByUsername(String username)
	{
		return clientsByUsername.get(username);
	}

	/**
	 * Called by ClientHandler when the client sets a username.
	 *
	 * @param username The username assigned to the ClientHandler.
	 * @param handler  The ClientHandler that has had a username set.
	 */
	@SuppressWarnings("unchecked")
	void clientUsernameSet(String username, ClientHandler handler)
	{
		clientsByUsername.put(username, (T) handler);
	}

	/**
	 * Get the current number of connected clients.
	 *
	 * @return The current number of connected clients.
	 */
	public int getNumClients()
	{
		return numClients;
	}
}
