package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageStreamPair;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens for incoming client connections on behalf of a Server.
 * @author Caleb Brinkman
 */
class ClientListener<T extends MessageContext> implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(ClientListener.class.getName());
	private final Class<? extends T> contextClass;
	private final int port;
	private final LinkedList<Connection<? extends T>> newConnections;
	private volatile boolean listening;
	private final ServerSocket serverSock;

	/**
	 * Construct a new ClientListener for the given server on the given port.
	 * @throws IOException If there is an error listening on the port.
	 * constructor.
	 */
	ClientListener(Class<? extends T> contextClass, int port) throws IOException
	{
		this.contextClass = contextClass;
		this.port = port;
		listening = false;
		newConnections = new LinkedList<>();
		LOGGER.log(Level.FINEST, "Opening socket on port: {0}", this.port);
		serverSock = new ServerSocket(this.port);
	}

	/**
	 * Get the new clients accrued since the last check.
	 * @return A {@code LinkedList} containing the new clients.
	 */
	public Iterable<Connection<? extends T>> getNewClients() {
		Collection<Connection<? extends T>> temp = new LinkedList<>();
		synchronized (newConnections)
		{
			if (!newConnections.isEmpty())
			{
				LOGGER.log(Level.FINE, newConnections.peek().getMessageContext().getName());
				temp = new LinkedList<>(newConnections);
				newConnections.removeAll(temp);
			}
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

	/**
	 * Listen for clients in a new thread. If already listening this method does nothing.
	 */
	public void startListening() {
		if (!listening)
		{
			listening = true;
			new Thread(this, "Client Listener " + port).start();
		}
	}

	/**
	 * Add a new client to the list of new clients.
	 * @param h The handler for the new client.
	 */
	void addNewClient(Connection<? extends T> h) {
		synchronized (newConnections)
		{
			newConnections.add(h);
		}
	}

	/**
	 * Add a new Client using the specified socket as a connection.
	 */
	private void addNewClient(MessageInputStream in, MessageOutputStream out) {
		T context = null;
		try
		{
			context = contextClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to instantiate context; missing default constructor?", e);
		}

		if (context != null)
		{
			MessageStreamPair messageStreamPair = new MessageStreamPair(in, out);
			Connection<T> newHandler = new Connection<>(messageStreamPair, context);
			addNewClient(newHandler);
		}
	}

	@Override
	public void run() {
		while (listening)
		{
			try
			{
				Socket sock = serverSock.accept();
				MessageInputStream in = new MessageInputStream(sock.getInputStream());
				MessageOutputStream out = new MessageOutputStream(sock.getOutputStream());
				addNewClient(in, out);
			} catch (SocketException ignored)
			{
			} catch (IOException e)
			{
				LOGGER.log(Level.WARNING, "Error connecting to client: ", e);
			}
		}
	}
}
