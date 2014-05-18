package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.WorldClientHandler;
import com.jenjinstudios.world.util.WorldServerMessageFactory;

/**
 * Process a WorldChecksumRequest.
 * @author Caleb Brinkman
 */
public class ExecutableWorldFileRequest extends WorldExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableWorldFileRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
	}

	@Override
	public void runSynced() {
	}

	@Override
	public void runASync() {
		byte[] worldFileBytes = getClientHandler().getServer().getWorldFileBytes();
		Message response = WorldServerMessageFactory.generateWorldFileResponse(getClientHandler(), worldFileBytes);
		getClientHandler().queueMessage(response);
	}
}
