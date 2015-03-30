package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;

/**
 * This class is the superclass for all ExecutableMessages that are invoked by the WorldClient.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldClientExecutableMessage extends ExecutableMessage
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
     */
    protected WorldClientExecutableMessage(WorldClient client, Message message) {
        super(client, message);
    }

	/**
	 * Get the WorldClient associated with this message.
	 *
	 * @return The WorldClient associated with this message.
	 */
	public WorldClient getWorldClient() {
		return (WorldClient) getThreadPool();
	}
}
