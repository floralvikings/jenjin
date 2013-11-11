package com.jenjinstudios.message;

import com.jenjinstudios.net.AuthClient;

/**
 * This class should be extended by ExecutableMessages intended to be run by an {@code AuthClient}.
 * @author Caleb Brinkman
 */
public abstract class AuthClientExecutableMessage extends ClientExecutableMessage
{
	/** The client invoking this ExecutableMessage. */
	private AuthClient client;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	protected AuthClientExecutableMessage(AuthClient client, Message message) {
		super(client, message);
		this.client = client;
	}

	/**
	 * Get the client invoking this ExecutableMessage.
	 * @return The client invoking this ExecutableMessage.
	 */
	public AuthClient getClient() {
		return client;
	}
}
