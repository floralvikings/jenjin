package com.jenjinstudios.jgsf;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.logging.Level;

/**
 * Listens for incoming client connections on behalf of a Server.
 *
 * @author Caleb Brinkman
 */
public abstract class ClientListener implements Runnable
{
	/** The port on which this object listens. */
	public final int PORT;
	/** The list of new clients. */
	private final LinkedList<ClientHandler> newClientHandlers;
	/** Flags whether this should be listening. */
	private volatile boolean listening;
	/** The server socket. */
	ServerSocket serverSock;
	/** The server. */
	private final Server server;

	/**
	 * Construct a new ClientListener for the given server on the given port.
	 *
	 * @param s The server for which this listener will listen.
	 * @param p The port on which to listen.
	 * @throws IOException If there is an error listening on the port.
	 */
	public ClientListener(Server s, int p) throws IOException
	{
		server = s;
		PORT = p;
		listening = false;
		newClientHandlers = new LinkedList<>();
		serverSock = new ServerSocket(PORT);
	}

	/**
	 * Get the new clients accrued since the last check.
	 *
	 * @return A {@code LinkedList} containing the new clients.
	 */
	public LinkedList<ClientHandler> getNewClients()
	{
		LinkedList<ClientHandler> temp = new LinkedList<>();
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
	public void addNewClient(ClientHandler h)
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
	public abstract void addNewClient(Socket sock);

	/**
	 * Get the server for which this listener is listening.
	 *
	 * @return The server for which this listener is listening.
	 */
	public Server getServer()
	{
		return server;
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
