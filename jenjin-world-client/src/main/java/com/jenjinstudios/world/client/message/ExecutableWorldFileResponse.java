package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;

/**
 * Handles login responses from the server.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldFileResponse extends WorldClientExecutableMessage<MessageContext>
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldFileResponse(WorldClient client, Message message, MessageContext context) {
		super(client, message, context);
	}

    @Override
	public Message execute() {
		byte[] bytes = (byte[]) getMessage().getArgument("fileBytes");
		getWorldClient().getServerWorldFileTracker().setBytes(bytes);
		getWorldClient().getServerWorldFileTracker().setWaitingForFile(false);
		return null;
	}
}
