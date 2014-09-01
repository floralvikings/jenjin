package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
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
	/** The logger used by this class. */
	public static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	/** The updates per second. */
	protected final int UPS;
	/** The period of the update in milliseconds. */
	protected final int PERIOD;
	/** The list of {@code ClientListener}s working for this server. */
	private final ClientListener<T> clientListener;
	/** The list of {@code ClientHandler}s working for this server. */
	private final Map<Integer, T> clientHandlers;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @throws java.io.IOException If there is an IO Error initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler
	 * constructor.
	 */
	@SuppressWarnings("unchecked")
	protected Server(ServerInit<T> initInfo) throws IOException, NoSuchMethodException {
		super("Server");
		LOGGER.log(Level.FINE, "Initializing Server.");
		UPS = initInfo.getUps();
		PERIOD = 1000 / UPS;
		clientListener = new ClientListener<>(getClass(), initInfo.getHandlerClass(), initInfo.getPort());
		clientHandlers = new TreeMap<>();
	}

	/**
	 * Add new clients that have connected to the client listeners.
	 */
	public void checkListenerForClients() {
		LinkedList<T> nc = clientListener.getNewClients();
		for (T h : nc)
		{
			addClientHandler(h);
			h.start();
		}

	}

	private void addClientHandler(T h) {
		int nullIndex = 0;
		while (clientHandlers.containsKey(nullIndex)) nullIndex++;
		clientHandlers.put(nullIndex, h);
		h.setHandlerId(nullIndex);
	}

	public void runClientHandlerQueuedMessages() {
		synchronized (clientHandlers)
		{
			Collection<T> handlers = clientHandlers.values();
			handlers.stream().filter(current -> current != null).forEach(Connection::runQueuedExecutableMessages);
		}
	}

	/** Broadcast all outgoing messages to clients. */
	public void broadcast() {
		synchronized (clientHandlers)
		{
			clientHandlers.values().stream().filter(current -> current != null).forEach(Connection::writeAllMessages);
		}
	}

	/** Update all clients before they sendAllMessages. */
	public void update() {
		synchronized (clientHandlers)
		{
			clientHandlers.values().stream().filter(current -> current != null).forEach(ClientHandler::update);
		}
	}

	/** Run the server. */
	@Override
	public void run() {
		clientListener.startListening(this);
	}

	/**
	 * Shutdown the server, forcing all client links to close.
	 * @throws IOException if there is an error shutting down a client.
	 */
	protected void shutdown() throws IOException {
		synchronized (clientHandlers)
		{
			clientHandlers.values().stream().filter(h -> h != null).forEach(ClientHandler::shutdown);
		}
		clientListener.stopListening();
	}

	/**
	 * Schedule a client to be removed during the next update.
	 * @param handler The client handler to be removed.
	 */
	protected void removeClient(ClientHandler handler) {
		synchronized (clientHandlers)
		{
			clientHandlers.remove(handler.getHandlerId());
		}
	}
}
