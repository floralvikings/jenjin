package com.jenjinstudios.server.net;

import com.jenjinstudios.core.connection.ConnectionConfig;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.concurrency.ConnectionAddedTask;
import com.jenjinstudios.server.concurrency.ShutdownTask;
import com.jenjinstudios.server.concurrency.UpdateTask;

import java.security.KeyPair;
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
	private ConnectionConfig<T> connectionConfig;
	private KeyPair keyPair;
	private int ups;

	private ServerConfig() {
		updateTasks = new LinkedList<>();
		connectionAddedTasks = new LinkedList<>();
		shutdownTasks = new LinkedList<>();
	}

	/**
	 * Get the KeyPair used for encryption and decryption.
	 *
	 * @return The KeyPair used for encryption and decryption.
	 */
	public KeyPair getKeyPair() { return keyPair; }

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
	 * Get the ConnectionConfig that will be used by connections to the server.
	 *
	 * @return The ConnectionConfig that will be used by connections to the server.
	 */
	public ConnectionConfig<T> getConnectionConfig() { return connectionConfig; }
}
