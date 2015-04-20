package com.jenjinstudios.server.concurrency;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageStreamPair;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens on a port for incoming connections, and creates a Connection object for them.
 *
 * @author Caleb Brinkman
 */
public class ConnectionListener<T extends MessageContext> implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(ConnectionListener.class.getName());
	private final Class<T> contextClass;
	private final Collection<Connection<T>> newConnections;
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private final ServerSocket serverSocket;

	/**
	 * Construct a new ConnectionListener, which will listen on the given port and pass each new connection an instance
	 * of contextClass.
	 *
	 * @param port The port number on which to listen.
	 * @param contextClass The class of MessageContext that will be passed into the new connections.
	 *
	 * @throws java.io.IOException If there is an error when creating the server socket.
	 */
	public ConnectionListener(int port, Class<T> contextClass) throws IOException
	{
		this.contextClass = contextClass;
		this.newConnections = new LinkedList<>();
		serverSocket = new ServerSocket(port);
	}

	/**
	 * Get any new connections since the last time this method was called.
	 *
	 * @return Any new connections made since the last time this method was called.
	 */
	public Iterable<Connection<T>> getNewConnections() {
		Collection<Connection<T>> temp = new LinkedList<>();
		synchronized (newConnections)
		{
			Iterator<Connection<T>> iterator = newConnections.iterator();
			while (iterator.hasNext())
			{
				temp.add(iterator.next());
				iterator.remove();
			}
		}
		return temp;
	}

	/**
	 * Start listening for new connections.
	 */
	public void start()
	{
		executorService.scheduleWithFixedDelay(this, 0, 10, TimeUnit.MILLISECONDS);
	}

	/**
	 * Stop listening for new connections.
	 */
	public void shutdown()
	{
		try
		{
			serverSocket.close();
		} catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to close server socket", e);
		}
		executorService.shutdown();
	}

	@Override
	public void run() {
		try
		{
			Socket socket = serverSocket.accept();
			MessageInputStream inputStream = new MessageInputStream(socket.getInputStream());
			MessageOutputStream outputStream = new MessageOutputStream(socket.getOutputStream());
			MessageStreamPair streamPair = new MessageStreamPair(inputStream, outputStream);
			Connection<T> connection = createConnection(streamPair);
			if (connection != null)
			{
				synchronized (newConnections)
				{
					newConnections.add(connection);
				}
			}
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Error connecting to client: ", e);
		}
	}

	private T createMessageContext() {
		T context = null;
		try
		{
			context = contextClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to instantiate context; missing default constructor?", e);
		}
		return context;
	}

	private Connection<T> createConnection(MessageStreamPair streamPair)
	{
		Connection<T> newConnection = null;
		T context = createMessageContext();
		if (context != null)
		{
			newConnection = new Connection<>(streamPair, context);
		}
		return newConnection;
	}
}
