package com.jenjinstudios.world.message;

import com.jenjinstudios.client.message.AuthClientExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.WorldClient;

/**
 * This class is the superclass for all ExecutableMessages that are invoked by the WorldClient.
 * @author Caleb Brinkman
 */
public abstract class WorldClientExecutableMessage extends AuthClientExecutableMessage
{
	/** The WorldClient invoking this message. */
	private final WorldClient client;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	protected WorldClientExecutableMessage(WorldClient client, Message message) {
		super(client, message);
		this.client = client;
	}

	@Override
	protected WorldClient getClient() {
		return client;
	}
}
