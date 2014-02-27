package com.jenjinstudios.net;

import com.jenjinstudios.io.MessageRegistry;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens for incoming client connections on behalf of a Server.
 * @author Caleb Brinkman
 */
class ClientListener<T extends ClientHandler> implements Runnable
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ClientListener.class.getName());
	/** The port on which this object listens. */
	private final int PORT;
	/** The list of new clients. */
	private final LinkedList<T> newClientHandlers;
	/** Flags whether this should be listening. */
	private volatile boolean listening;
	/** The server socket. */
	private ServerSocket serverSock;
	/** The server. */
	private Server server;
	/** The constructor called to create new handlers. */
	private Constructor<T> handlerConstructor;

	/**
	 * Construct a new ClientListener for the given server on the given port.
	 * @param serverClass The server for which this listener will listen.
	 * @param p The port on which to listen.
	 * @param handlerClass The class of the ClientHandler to be used by this server.
	 * @throws IOException If there is an error listening on the port.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler constructor.
	 */
	public ClientListener(Class<? extends Server> serverClass, int p, Class<T> handlerClass) throws IOException, NoSuchMethodException {
		PORT = p;
		/* The class of client handlers created by this listener. */
		try
		{
			handlerConstructor = handlerClass.getConstructor(serverClass, Socket.class, MessageRegistry.class);
		} catch (NoSuchMethodException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to find appropriate ClientHandler constructor: " + handlerClass.getName(), e);
			throw e;
		}
		listening = false;
		newClientHandlers = new LinkedList<>();
		serverSock = new ServerSocket(PORT);
	}

	/**
	 * Get the new clients accrued since the last check.
	 * @return A {@code LinkedList} containing the new clients.
	 */
	public LinkedList<T> getNewClients() {
		LinkedList<T> temp = new LinkedList<>();
		synchronized (newClientHandlers)
		{
			if (newClientHandlers.isEmpty())
				return temp;
			Server.LOGGER.log(Level.FINE, newClientHandlers.peek().toString());
			temp = new LinkedList<>(newClientHandlers);
			newClientHandlers.removeAll(temp);
		}
		return temp;
	}

	/**
	 * Stop listening and close the socket.
	 * @throws IOException If there is an error closing the socket.
	 */
	public void stopListening() throws IOException {
		listening = false;
		serverSock.close();
	}

	/** Listen for clients in a new thread. If already listening this method does nothing.
	 * @param tServer The server */
	public void startListening(Server<T> tServer) {
		if (listening)
			return;
		this.server = tServer;
		listening = true;
		new Thread(this, "Client Listener " + PORT).start();
	}

	/**
	 * Add a new client to the list of new clients.
	 * @param h The handler for the new client.
	 */
	void addNewClient(T h) {
		synchronized (newClientHandlers)
		{
			newClientHandlers.add(h);
		}
	}

	/**
	 * Add a new Client using the specified socket as a connection.
	 * @param sock The connection to the new client.
	 */
	private void addNewClient(Socket sock) {
		try
		{
			T newHandler = handlerConstructor.newInstance(server, sock, server.getMessageRegistry());
			addNewClient(newHandler);
		} catch (InstantiationException | IllegalAccessException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to instantiate client handler:", e);
		} catch (InvocationTargetException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to instantiate client handler:", e.getCause());
		}
	}

	@Override
	public void run() {
		while (listening)
		{
			try
			{
				Socket sock = serverSock.accept();
				addNewClient(sock);
			} catch (SocketException ignored)
			{
			} catch (IOException e)
			{
				Server.LOGGER.log(Level.WARNING, "Error connecting to client: ", e);
			}
		}
	}
}
