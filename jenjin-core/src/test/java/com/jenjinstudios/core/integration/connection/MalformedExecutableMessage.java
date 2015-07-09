package com.jenjinstudios.core.integration.connection;

import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;

/**
 * Used for testing the reception of an invalid message.
 *
 * @author Caleb Brinkman
 */
public class MalformedExecutableMessage extends ExecutableMessage<MessageContext>
{
	/**
	 * Demonstrates an invalid constructor.
	 */
	protected MalformedExecutableMessage() {
		super(null, null);
	}

	@Override
	public Message execute() {
		return null;
	}
}
