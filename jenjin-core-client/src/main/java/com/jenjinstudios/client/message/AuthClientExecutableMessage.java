package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.core.io.Message;

/**
 * This class should be extended by ExecutableMessages intended to be run by an {@code AuthClient}.
 * @author Caleb Brinkman
 */
public abstract class AuthClientExecutableMessage extends ClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	protected AuthClientExecutableMessage(AuthClient client, Message message) {
		super(client, message);
	}

	/**
	 * Get the client invoking this ExecutableMessage.
	 * @return The client invoking this ExecutableMessage.
	 */
	@Override
	protected AuthClient getClient() {
		return (AuthClient) super.getClient();
	}
}
