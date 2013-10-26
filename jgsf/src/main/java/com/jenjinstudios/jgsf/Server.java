package com.jenjinstudios.jgsf;

import com.jenjinstudios.message.MessageRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
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
	/** Tasks to be repeated in the main loop. */
	private final LinkedList<Runnable> repeatedTasks;
	/** Synced tasks scheduled by client handlers. */
	private final LinkedList<Runnable> syncedTasks;
	/** The maximum number of clients allowed to connect. */
	private int maxClients = 100;
	/** The current number of connected clients. */
	private int numClients;
	/** The class for ClientHandlers. */
	private final Class<? extends T> handlerClass;
	/** The timer that controls the server loop. */
	private Timer loopTimer;
	/** The server loop. */
	private ServerLoop serverLoop;
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
		for (int i = 0; i < maxClients; i++)
			clientHandlers.add(null);
		repeatedTasks = new LinkedList<>();
		syncedTasks = new LinkedList<>();
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
	 * Get the start time, in nanoseconds, of the current update cycle.
	 *
	 * @return The cycle start time.
	 */
	public long getCycleStartTime()
	{
		return serverLoop != null ? serverLoop.getCycleStart() : -1;
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
	 * Add a task to be repeated every update.
	 *
	 * @param r The {@code Runnable} containing the task to be repeated.
	 */
	@SuppressWarnings("unused")
	public void addRepeatedTask(Runnable r)
	{
		synchronized (repeatedTasks)
		{
			repeatedTasks.add(r);
		}
	}

	/**
	 * Add an ExecutableMessage to the synced tasks list.
	 *
	 * @param r The {@code ExecutableMessage} to add.
	 */
	public void addSyncedTask(Runnable r)
	{
		synchronized (syncedTasks)
		{
			syncedTasks.add(r);
		}
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
					syncedTasks.addAll(current.getSyncedTasks());
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
	public final void run()
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

		serverLoop = new ServerLoop(this);

		/* The name of the timer that is looping the server thread. */
		String timerName = "SqlEnabledServer Update Loop";
		loopTimer = new Timer(timerName, false);
		loopTimer.scheduleAtFixedRate(serverLoop, 0, PERIOD);

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
		if (loopTimer != null)
			loopTimer.cancel();
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

	/**
	 * The actual average UPS of this server.
	 *
	 * @return The average UPS of this server
	 */
	public double getAverageUPS()
	{
		return serverLoop.getAverageUPS();
	}

	/**
	 * Tasks to be repeated in the main loop.
	 *
	 * @return The list of repeated tasks to be executed by this server.
	 */
	LinkedList<Runnable> getRepeatedTasks()
	{
		return repeatedTasks;
	}

	/**
	 * Synced tasks scheduled by client handlers.
	 *
	 * @return The list of syncrhonized tasks scheduled by ClientHandlers.
	 */
	LinkedList<Runnable> getSyncedTasks()
	{
		return syncedTasks;
	}

	/**
	 * Get the maximum number of clients allowed to connect to this server.
	 *
	 * @return The maximum number of clients allowed to connect to this server.
	 */
	public int getMaxClients()
	{
		return maxClients;
	}

	/**
	 * Set the maximum number of clients allowed to connect to this server.
	 *
	 * @param maxClients The new maximum number of clients alowed to connect to this server.
	 */
	public void setMaxClients(int maxClients)
	{
		int diff = maxClients - this.maxClients;
		if (diff < 0 && !clientHandlers.isEmpty())
			throw new IndexOutOfBoundsException("Cannot make client array smaller.");
		synchronized (clientHandlers)
		{
			for (int i = 0; i < Math.abs(diff); i++)
				if (diff >= 0) clientHandlers.add(null);
				else clientHandlers.remove(0);
		}
		this.maxClients = maxClients;
	}
}
