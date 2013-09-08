package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.WorldClient;
import com.jenjinstudios.message.Message;

/**
 * This class is the superclass for all ExecutableMessages that are invoked by the WorldClient.
 *
 * @author Caleb Brinkman
 */
public abstract class ClientWorldExecutableMessage extends ClientExecutableMessage
{
	/** The WorldClient invoking this message. */
	private WorldClient client;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param client  The client invoking this message.
	 * @param message The Message.
	 */
	protected ClientWorldExecutableMessage(WorldClient client, Message message)
	{
		super(client, message);
		this.client = client;
	}

	@Override
	public WorldClient getClient()
	{
		return client;
	}
}
