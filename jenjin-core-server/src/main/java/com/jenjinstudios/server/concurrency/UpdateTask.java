package com.jenjinstudios.server.concurrency;

import com.jenjinstudios.core.Connection;

/**
 * Called at regular intervals on Connections in a ConnectionPool.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface UpdateTask<T extends Connection>
{
	/**
	 * Perform some sort of update on the specified connection.
	 *
	 * @param connection The connection.
	 */
	void update(T connection);
}
