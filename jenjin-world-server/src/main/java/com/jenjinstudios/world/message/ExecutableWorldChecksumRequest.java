package com.jenjinstudios.world.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.WorldClientHandler;

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
	public void runSynced() {
	}

	@Override
	public void runASync() {
		byte[] checkSum = getClientHandler().getServer().getWorldFileChecksum();
		Message response = getClientHandler().getMessageFactory().generateWorldChecksumResponse(checkSum);
		getClientHandler().queueMessage(response);
	}

}
