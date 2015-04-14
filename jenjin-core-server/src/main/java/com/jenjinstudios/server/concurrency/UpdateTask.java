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
	void update(T connection);
}
