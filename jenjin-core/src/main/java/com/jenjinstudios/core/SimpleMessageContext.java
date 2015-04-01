package com.jenjinstudios.core;

import com.jenjinstudios.core.concurrency.MessageContext;

/**
 * Represents the most basic implemented MessageContext.
 *
 * @author Caleb Brinkman
 */
public class SimpleMessageContext implements MessageContext
{
	private final String name;
	private final PingTracker pingTracker;

	/**
	 * Construct a new MessageContext with the given name.
	 *
	 * @param name The name of the context.
	 */
	public SimpleMessageContext(String name) {
		this.name = name;
		this.pingTracker = new PingTracker();
	}

	@Override
	public String getName() { return name; }

	/**
	 * Get the PingTracker associated with this MessageContext.
	 *
	 * @return The PingTracker associated with this MessageContext.
	 */
	public PingTracker getPingTracker() { return pingTracker; }
}
