package com.jenjinstudios.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base Server class for implementation of the JGSA.  It contains extensible execution functionality designed to be
 * used by Executable Messages from ClientHandlers.
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class Server<T extends ClientHandler> extends Thread
{
	/** The default number of max clients. */
	public static final int DEFAULT_MAX_CLIENTS = 100;
	/** The logger used by this class. */
	public static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	/** The number of milliseconds before a blocking method should time out. */
	public static long TIMEOUT_MILLIS = 30000;
	/** The updates per second. */
	public final int UPS;
	/** The period of the update in milliseconds. */
	public final int PERIOD;
	/** The list of {@code ClientListener}s working for this server. */
	private final ClientListener<T> clientListener;
	/** The list of {@code ClientHandler}s working for this server. */
	private final ArrayList<T> clientHandlers;
	/** The map of clients stored by username. */
	private final TreeMap<String, T> clientsByUsername;
	/** Indicates whether this server is initialized. */
	private volatile boolean initialized;
	/** The current number of connected clients. */
	private int numClients;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @param ups The cycles per second at which this server will run.
	 * @param port The port number on which this server will listen.
	 * @param handlerClass The class of ClientHandler used by this Server.
	 * @throws java.io.IOException If there is an IO Error initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler constructor.
	 */
	public Server(int ups, int port, Class<? extends T> handlerClass) throws IOException, NoSuchMethodException {
		this(ups, port, handlerClass, DEFAULT_MAX_CLIENTS);
	}

	/**
	 * Construct a new Server without a SQLHandler.
	 * @param ups The cycles per second at which this server will run.
	 * @param port The port number on which this server will listen.
	 * @param handlerClass The class of ClientHandler used by this Server.
	 * @param maxClients The maximum number of clients.
	 * @throws java.io.IOException If there is an IO Error initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler constructor.
	 */
	@SuppressWarnings("unchecked")
	public Server(int ups, int port, Class<? extends T> handlerClass, int maxClients) throws IOException, NoSuchMethodException {
		super("Server");
		LOGGER.log(Level.FINE, "Initializing Server.");
		UPS = ups;
		PERIOD = 1000 / ups;
		clientsByUsername = new TreeMap<>();
		clientListener = (ClientListener<T>) new ClientListener<>(getClass(), port, handlerClass);
		clientHandlers = new ArrayList<>();
		for (int i = 0; i < maxClients; i++)
			clientHandlers.add(null);
		numClients = 0;
	}

	/**
	 * Schedule a client to be removed during the next update.
	 * @param handler The client handler to be removed.
	 */
	void removeClient(ClientHandler handler) {
		synchronized (clientHandlers)
		{
			String username = handler.getUsername();
			if (username != null) { clientsByUsername.remove(username); }
			clientHandlers.set(handler.getHandlerId(), null);
			numClients--;
		}
	}

	/**
	 * Add new clients that have connected to the client listeners.
	 * @return true if new clients were added.
	 */
	public boolean getNewClients() {
		boolean clientsAdded;
		LinkedList<T> nc = clientListener.getNewClients();
		clientsAdded = !nc.isEmpty();
		for (T h : nc)
		{
			int nullIndex = clientHandlers.indexOf(null);
			clientHandlers.set(nullIndex, h);
			h.setID(nullIndex);
			h.start();
			numClients++;
		}

		return clientsAdded;
	}

	/** Broadcast all outgoing messages to clients. */
	public void broadcast() {
		synchronized (clientHandlers)
		{
			for (ClientHandler current : clientHandlers)
			{
				if (current != null) { current.sendAllMessages(); }
			}
		}
	}

	/** Update all clients before they sendAllMessages. */
	public void update() {
		synchronized (clientHandlers)
		{
			for (ClientHandler current : clientHandlers)
			{
				if (current != null) { current.update(); }
			}
		}
	}

	/** Refresh all clients after they sendAllMessages. */
	public void refresh() {
		synchronized (clientHandlers)
		{
			for (ClientHandler current : clientHandlers)
			{
				if (current != null) { current.refresh(); }
			}
		}
	}

	/** Run the server. */
	@Override
	public void run() {
		clientListener.startListening(this);
		initialized = true;
	}

	/**
	 * Start the server, and do not return until it is fully initialized.
	 * @return If the blocking start was successful.
	 */
	public final boolean blockingStart() {
		long startTime = System.currentTimeMillis();
		long timepast = System.currentTimeMillis() - startTime;
		start();
		while (!initialized && (timepast < TIMEOUT_MILLIS))
		{
			try
			{
				Thread.sleep(10);
				timepast = System.currentTimeMillis() - startTime;
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Server blocking start was interrupted.", e);
			}
		}
		return initialized;
	}

	/**
	 * Shutdown the server, forcing all client links to close.
	 * @throws IOException if there is an error shutting down a client.
	 */
	public void shutdown() throws IOException {
		synchronized (clientHandlers)
		{
			for (ClientHandler h : clientHandlers)
			{
				if (h != null) { h.shutdown(); }
			}
		}
		clientListener.stopListening();
	}

	/**
	 * Return whether this server is initialized.
	 * @return true if the server has been initialized.
	 */
	public boolean isInitialized() { return initialized; }

	/**
	 * Get the ClientHandler with the given username.
	 * @param username The username of the client to look up.
	 * @return The client with the username specified; null if there is no client with this username.
	 */
	public T getClientHandlerByUsername(String username) { return clientsByUsername.get(username); }

	/**
	 * Called by ClientHandler when the client sets a username.
	 * @param username The username assigned to the ClientHandler.
	 * @param handler The ClientHandler that has had a username set.
	 */
	@SuppressWarnings("unchecked")
	void clientUsernameSet(String username, ClientHandler handler) { clientsByUsername.put(username, (T) handler); }

	/**
	 * Get the current number of connected clients.
	 * @return The current number of connected clients.
	 */
	public int getNumClients() {
		return numClients;
	}
}
