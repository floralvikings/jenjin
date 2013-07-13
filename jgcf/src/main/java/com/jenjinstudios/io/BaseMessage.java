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
	private final short ID;

	/**
	 * Construct a new message using the given ID and arguments.
	 *
	 * @param args The arguments used to create the message.
	 */
	protected BaseMessage(short id, Object... args)
	{
		this.args = args;
		this.ID = id;
	}

	/**
	 * Get the ID of this message.
	 *
	 * @return The ID of this message
	 */
	public short getID()
	{
		return ID;
	}

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
