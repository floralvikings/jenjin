package com.jenjinstudios.server.concurrency;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.connection.Connection;
import com.jenjinstudios.core.connection.ConnectionInstantiationException;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;

import java.io.IOException;
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
    private final Collection<Connection<T>> newConnections;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ServerSocket serverSocket;
    private final Class<T> contextClass;

    /**
     * Construct a new ConnectionListener that will provide incoming connections with the given configuration.
     *
     * @param contextClass The context class for incoming connections.
     * @param port The port number on which to listen for connections.
     *
     * @throws IOException If there's an exception when setting up a server socket.
     */
    public ConnectionListener(Class<T> contextClass, int port) throws IOException {
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
        synchronized (newConnections) {
            Iterator<Connection<T>> iterator = newConnections.iterator();
            while (iterator.hasNext()) {
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
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to close server socket", e);
        }
        executorService.shutdown();
    }

    @Override
    public void run() {
        try {
            Socket socket = serverSocket.accept();
            MessageInputStream inputStream = new MessageInputStream(socket.getInputStream());
            MessageOutputStream outputStream = new MessageOutputStream(socket.getOutputStream());
            Connection<T> connection = createConnection(inputStream, outputStream);
            if (connection != null) {
                synchronized (newConnections) {
                    newConnections.add(connection);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error connecting to client: ", e);
        }
    }

    private Connection<T> createConnection(MessageInputStream inputStream, MessageOutputStream outputStream)
    {
        Connection<T> newConnection = null;
        try {
            newConnection = new Connection<>(contextClass, inputStream, outputStream);
        } catch (ConnectionInstantiationException e) {
            LOGGER.log(Level.WARNING, "Unable to instantiate connection", e);
        }
        return newConnection;
    }
}
