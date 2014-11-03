package com.jenjinstudios.core.io;

import java.io.IOException;

/**
 * Used when a {@code MessageInputStream} reads a message that has not been registered.
 *
 * @author Caleb Brinkman
 */
public class MessageTypeException extends IOException
{
	private final short id;

	/**
	 * Construct a new {@code MessageTypeException} when an invalid message type is read with the given id.
	 *
	 * @param id The id of the invalid message type.
	 */
	public MessageTypeException(short id) {
		super("Message " + id + " not registered.");
		this.id = id;
	}

	/**
	 * Get the id of the invalid message that caused this exception.
	 *
	 * @return The id of the invalid message that caused this exception.
	 */
	public short getId() { return id; }
}
