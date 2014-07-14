package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.server.WorldClientHandler;

/**
 * Process a WorldChecksumRequest.
 * @author Caleb Brinkman
 */
public class ExecutableWorldChecksumRequest extends WorldExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableWorldChecksumRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
	}

	@Override
	public void runDelayed() {
	}

	@Override
	public void runImmediate() {
		byte[] checkSum = getClientHandler().getServer().getWorldFileChecksum();
		Message response = getClientHandler().getMessageFactory().generateWorldChecksumResponse(checkSum);
		getClientHandler().queueOutgoingMessage(response);
	}

}
