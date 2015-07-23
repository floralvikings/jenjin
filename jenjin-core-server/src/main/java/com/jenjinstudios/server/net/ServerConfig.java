package com.jenjinstudios.server.net;

import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.concurrency.ConnectionAddedTask;
import com.jenjinstudios.server.concurrency.ShutdownTask;
import com.jenjinstudios.server.concurrency.UpdateTask;

import java.util.Collection;
import java.util.LinkedList;

import static java.util.Collections.unmodifiableCollection;

/**
 * Used for configuring a Server.
 *
 * @author Caleb Brinkman
 */
public class ServerConfig<U extends User, T extends ServerMessageContext<U>>
{
	private final Collection<UpdateTask<T>> updateTasks;
	private final Collection<ConnectionAddedTask<T>> connectionAddedTasks;
	private final Collection<ShutdownTask<T>> shutdownTasks;
    private Class<T> contextClass;
    private Authenticator<U> authenticator;
    private int ups;
    private int port;

	private ServerConfig() {
		updateTasks = new LinkedList<>();
		connectionAddedTasks = new LinkedList<>();
		shutdownTasks = new LinkedList<>();
	}

    /**
     * Get the updates per second at which the server should update.
	 *
	 * @return The updates per second at which the server should update.
	 */
	public int getUps() { return ups; }

	/**
	 * Get the update tasks to be run by the server.
	 *
	 * @return The update tasks to be run by the server.
	 */
	public Collection<UpdateTask<T>> getUpdateTasks() { return unmodifiableCollection(updateTasks); }

	/**
	 * Get the connection added tasks to be run by the server.
	 *
	 * @return The connection added tasks to be run by the server.
	 */
	public Collection<ConnectionAddedTask<T>> getConnectionAddedTasks() {
		return unmodifiableCollection(connectionAddedTasks);
	}

	/**
	 * Get the shutdown tasks to be run by the server.
	 *
	 * @return The shutdown tasks to be run by the server.
	 */
	public Collection<ShutdownTask<T>> getShutdownTasks() { return unmodifiableCollection(shutdownTasks); }

    /**
     * Get the Authenticator used to authenticate users for the server.
     *
     * @return The Authenticator used to authenticate users for the server.
     */
    public Authenticator<U> getAuthenticator() { return authenticator; }

    /**
     * Get the class used to instantiate message contexts for this server.
     *
     * @return The class used to instantiate message contexts for this server.
     */
    public Class<T> getContextClass() { return contextClass; }

    /**
     * Get the port number on which the server will listen for connections.
     *
     * @return The port number on which the server will listen for connections.
     */
    public int getPort() { return port; }

}
