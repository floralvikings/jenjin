package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.concurrency.BroadcastMessage;
import com.jenjinstudios.server.concurrency.ConnectionPool;
import com.jenjinstudios.server.concurrency.UpdateTask;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages a ConnectionPool which listens for incoming connections an executes tasks on them as dictated by the tasks
 * added and the Authenticator used to authenticate users.
 *
 * @param <U> The type of user that will be added in this server.
 * @param <C> The type of MessageContext that will be passed to messages received by connections on this server.
 */
public class Server<U extends User, C extends ServerMessageContext<U>>
{
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	private final ConnectionPool<C> connectionPool;
	private final int ups;

    /** Used by Gson. */
    private Server() throws IOException { this(null); }

	/**
	 * Construct a new Server.
	 *
	 * @param config The configuration for incoming connections.
	 *
	 * @throws IOException If there is an error registering messages.
	 */
    public Server(ServerConfig<U, C> config) throws IOException
    {
        LOGGER.log(Level.FINE, "Initializing Server.");
		ups = config.getUps();
        connectionPool = new ConnectionPool<>(config.getContextClass(), config.getPort());
        connectionPool.addUpdateTask(new BroadcastTask());
		connectionPool.addShutdownTask(new EmergencyLogoutTask<>());
        connectionPool.addConnectionAddedTask(connection ->
                    connection.getMessageContext().setAuthenticator(config.getAuthenticator())
        );
        config.getConnectionAddedTasks().forEach(connectionPool::addConnectionAddedTask);
		config.getUpdateTasks().forEach(connectionPool::addUpdateTask);
		config.getShutdownTasks().forEach(connectionPool::addShutdownTask);
	}


	/**
	 * Get the updates per second of this server.
	 *
	 * @return The updates per second of this server.
	 */
	// TODO This should probably be removed, folded into World since that's the only time it's used.
	public int getUps() { return ups; }

	/**
	 * Start listening for and maintaining connections.
	 */
	public void start() {
		connectionPool.start();
	}

	/**
	 * Shutdown all active connections and stop listening for new ones.
	 *
	 * @throws IOException If there's an exception when whutting down clients.
	 */
	public void shutdown() throws IOException {
		connectionPool.shutdown();
	}

    private class BroadcastTask implements UpdateTask<C>
    {

		@Override
		public void update(Connection<? extends C> connection) {
			Collection<BroadcastMessage> broadcasts = connection.getMessageContext().getBroadcasts();
			broadcasts.forEach(connectionPool::broadcastMessage);
		}
	}

}
