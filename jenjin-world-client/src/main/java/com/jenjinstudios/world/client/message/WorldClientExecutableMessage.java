package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.message.AuthClientExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;

/**
 * This class is the superclass for all ExecutableMessages that are invoked by the WorldClient.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldClientExecutableMessage extends AuthClientExecutableMessage
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

    @Override
    public WorldClient getConnection() {
        return (WorldClient) super.getConnection();
    }
}
