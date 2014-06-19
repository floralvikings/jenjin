package com.jenjinstudios.message;

import com.jenjinstudios.core.io.ExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.net.Client;

/**
 * Superclass of ExecutableMessages used by the client.
 * @author Caleb Brinkman
 */
public abstract class ClientExecutableMessage extends ExecutableMessage
{
	/** The client invoking this ExecutableMessage. */
	private final Client client;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	ClientExecutableMessage(Client client, Message message) {
		super(message);
		this.client = client;
	}

	/**
	 * Get the client invoking this ExecutableMessage.
	 * @return The client invoking this ExecutableMessage.
	 */
	Client getClient() {
		return client;
	}
}
