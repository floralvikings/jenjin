package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClientMessageContext;

/**
 * Handles login responses from the server.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldFileResponse extends WorldClientExecutableMessage<WorldClientMessageContext>
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldFileResponse(Message message, WorldClientMessageContext context) {
		super(context);
	}

    @Override
	public Message execute() {
		byte[] bytes = (byte[]) getMessage().getArgument("fileBytes");
		getContext().getWorldFileTracker().setBytes(bytes);
		getContext().getWorldFileTracker().setWaitingForFile(false);
		return null;
	}
}
