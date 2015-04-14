package com.jenjinstudios.core.concurrency;

/**
 * Used to represent a task which will be invoked when a thread pool is shut down.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface ShutdownTask
{
	/**
	 * When added to a MessageThreadPool, this method will be called with the MessageThreadPool passed as a parameter.
	 *
	 * @param threadPool The MessageThreadPool being shut down.
	 */
	void shutdown(MessageThreadPool threadPool);
}
