package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;
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
     * @param client The client invoking this message.
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldChecksumResponse(WorldClient client, Message message, WorldClientMessageContext context) {
		super(client, message, context);
	}

    @Override
	public Message execute() {
		byte[] bytes = (byte[]) getMessage().getArgument("checksum");
		getWorldClient().getServerWorldFileTracker().setChecksum(bytes);
		getWorldClient().getServerWorldFileTracker().setWaitingForChecksum(false);
		return null;
	}
}
