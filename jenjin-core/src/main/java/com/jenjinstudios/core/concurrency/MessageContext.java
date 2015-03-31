package com.jenjinstudios.core.concurrency;

/**
 * Used to represent data that should be passed into an executable message on construction.
 *
 * @author Caleb Brinkman
 */
public interface MessageContext
{
	/**
	 * Get the name of the context.
	 *
	 * @return The name of the context.
	 */
	String getName();
}
