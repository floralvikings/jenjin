package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;

/**
 * @author Caleb Brinkman
 */
public class MessageQueueException extends RuntimeException
{
	public MessageQueueException(Message message) {
		super("Attempting to queue message while stream closed: " + message.name);
	}
}
