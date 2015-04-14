package com.jenjinstudios.server.concurrency;

import com.jenjinstudios.core.Connection;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Maintains a list of and monitors a collection of Connections.
 *
 * @author Caleb Brinkman
 */
public class ConnectionPool<T extends Connection>
{
	private final Map<String, T> connections = new HashMap<>(1);
	private final Timer cleanupTimer = new Timer("Connection Pool Cleanup Timer");
	private final TimerTask cleanupTask = new CleanupTask();

	/**
	 * Add a connection to the pool.
	 *
	 * @param connection The connection to add.
	 */
	public void addConnection(T connection)
	{
		synchronized (connections)
		{
			connections.put(connection.getId(), connection);
		}
	}

	/**
	 * Remove a connection from the pool.
	 *
	 * @param connection The connection to remove.
	 */
	public void removeConnection(T connection)
	{
		synchronized (connections)
		{
			connections.remove(connection.getId());
		}
	}

	/**
	 * Shutdown all connections in the pool.
	 */
	public void shutdownConnections()
	{
		synchronized (connections)
		{
			connections.forEach((key, value) -> value.shutdown());
		}
	}

	/**
	 * Shut down all connections and maintainence threads, removing connections from the pool when finished.
	 */
	public void shutdown() {
		shutdownConnections();
		cleanupTimer.cancel();
		cleanupTask.run();
	}

	/**
	 * Start maintanence threads; responsible for updating and removing connections that have been added and shut down.
	 */
	public void start() {
		cleanupTimer.schedule(cleanupTask, 0, 100);
	}

	private class CleanupTask extends TimerTask
	{
		@Override
		public void run() {
			List<T> shutdown;
			synchronized (connections)
			{
				shutdown = connections.values().stream().
					  filter((con) -> !con.isRunning()).
					  collect(Collectors.toList());
			}
			shutdown.forEach(ConnectionPool.this::removeConnection);
		}
	}
}
