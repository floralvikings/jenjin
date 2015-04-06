package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClientMessageContext;

/**
 * Handles login responses from the server.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldChecksumResponse extends WorldClientExecutableMessage<WorldClientMessageContext>
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldChecksumResponse(Message message, WorldClientMessageContext context) {
		super(context);
	}

    @Override
	public Message execute() {
		byte[] bytes = (byte[]) getMessage().getArgument("checksum");
		getContext().getWorldFileTracker().setChecksum(bytes);
		getContext().getWorldFileTracker().setWaitingForChecksum(false);
		return null;
	}
}
