package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;

/**
 * Runtime exception thrown when an attempt is made to write to a closed MessageOutputStream.
 * @author Caleb Brinkman
 */
public class MessageQueueException extends RuntimeException
{
	/**
	 * Construct a new {@code MessageQueueException}, caused by attempting to write the specified message.
	 *
	 * @param message The {@code Message} that was attempted to be written to the closed stream.
	 */
	public MessageQueueException(Message message) {
		super("Attempting to queue message while stream closed: " + message.name);
	}
}
