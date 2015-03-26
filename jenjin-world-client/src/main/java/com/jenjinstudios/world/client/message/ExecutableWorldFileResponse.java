package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;

/**
 * Handles login responses from the server.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldFileResponse extends WorldClientExecutableMessage
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
     */
    public ExecutableWorldFileResponse(WorldClient client, Message message) {
        super(client, message);
    }

    @Override
    public void runImmediate() {
        byte[] bytes = (byte[]) getMessage().getArgument("fileBytes");
        getConnection().getServerWorldFileTracker().setBytes(bytes);
        getConnection().getServerWorldFileTracker().setWaitingForFile(false);
    }
}
