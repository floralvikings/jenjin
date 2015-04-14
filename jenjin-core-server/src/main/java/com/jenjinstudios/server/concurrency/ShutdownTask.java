package com.jenjinstudios.server.concurrency;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.MessageContext;

/**
 * Used to represent a task which will be invoked when a thread pool is shut down.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface ShutdownTask<T extends MessageContext>
{
	/**
	 * When added to a MessageThreadPool, this method will be called with the MessageThreadPool passed as a parameter.
	 *
	 * @param connection The MessageThreadPool being shut down.
	 */
	void shutdown(Connection<? extends T> connection);
}
