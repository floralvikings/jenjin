package com.jenjinstudios.server.concurrency;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.MessageContext;

/**
 * Invoked when a Connection is added to a connection pool.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface ConnectionAddedTask<T extends MessageContext>
{
	/**
	 * Called when a connection is added to the ConnectionPool.
	 *
	 * @param connection The connection that has been added.
	 */
	void connectionAdded(Connection<T> connection);
}
