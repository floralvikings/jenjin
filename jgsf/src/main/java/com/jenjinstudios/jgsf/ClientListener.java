package com.jenjinstudios.jgsf;

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
 *
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
	private final Server server;
	/** The constructor called to create new handlers. */
	private Constructor<T> handlerConstructor;

	/**
	 * Construct a new ClientListener for the given server on the given port.
	 *
	 * @param s            The server for which this listener will listen.
	 * @param p            The port on which to listen.
	 * @param handlerClass The class of the ClientHandler to be used by this server.
	 * @throws IOException If there is an error listening on the port.
	 */
	public ClientListener(Server s, int p, Class<T> handlerClass) throws IOException
	{
		server = s;
		PORT = p;
		/* The class of client handlers created by this listener. */
		try
		{
			handlerConstructor = handlerClass.getConstructor(s.getClass(), Socket.class);

		} catch (NoSuchMethodException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to find ClientHandler constructor: " + handlerClass.getName(), e);
			System.exit(0);
		}
		listening = false;
		newClientHandlers = new LinkedList<>();
		serverSock = new ServerSocket(PORT);
	}

	/**
	 * Get the new clients accrued since the last check.
	 *
	 * @return A {@code LinkedList} containing the new clients.
	 */
	public LinkedList<T> getNewClients()
	{
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
	 *
	 * @throws IOException If there is an error closing the socket.
	 */
	public void stopListening() throws IOException
	{
		listening = false;
		serverSock.close();
	}

	/** Listen for clients in a new thread. If already listening this method does nothing. */
	public void listen()
	{
		if (listening)
			return;
		listening = true;
		new Thread(this, "Client Listener " + PORT).start();
	}

	/**
	 * Add a new client to the list of new clients.
	 *
	 * @param h The handler for the new client.
	 */
	void addNewClient(T h)
	{
		synchronized (newClientHandlers)
		{
			newClientHandlers.add(h);
		}
	}

	/**
	 * Add a new Client using the specified socket as a connection.
	 *
	 * @param sock The connection to the new client.
	 */
	void addNewClient(Socket sock)
	{
		try
		{
			T newHandler = handlerConstructor.newInstance(server, sock);

			addNewClient(newHandler);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to instantiate client handler!", e);
		}
	}

	@Override
	public void run()
	{
		while (listening)
		{
			try
			{
				Socket sock = serverSock.accept();
				addNewClient(sock);
			} catch (SocketException ex)
			{
				// Socket is closed, no worries.  Just means we stopped listening =P
			} catch (IOException e)
			{
				Server.LOGGER.log(Level.WARNING, "Error", e);
			}
		}
	}
}
