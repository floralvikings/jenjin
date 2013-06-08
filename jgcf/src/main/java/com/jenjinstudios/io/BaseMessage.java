package com.jenjinstudios.io;

import java.io.Serializable;

/**
 * The base for all message classes that can be registered for the JGC and JGSA.
 *
 * @author Caleb Brinkman
 */
public abstract class BaseMessage implements Serializable
{
	/** The arguments to be passed to the message. */
	private final Object[] args;

	/**
	 * Construct a new message using the given ID and arguments.
	 *
	 * @param args The arguments used to create the message.
	 */
	protected BaseMessage(Object... args)
	{
		this.args = args;
	}

	/**
	 * Get the ID of this message.
	 *
	 * @return The ID of this message
	 */
	public abstract short getID();

	/**
	 * Get the arguments for this message.
	 *
	 * @return The arguments for this message.
	 */
	public final Object[] getArgs()
	{
		return args;
	}
}
