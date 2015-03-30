package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;

/**
 * Process a WorldChecksumRequest.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldFileRequest extends WorldExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableWorldFileRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
	}

	@Override
	public Message execute() {
		byte[] worldFileBytes = ((WorldServer) getClientHandler().getServer()).getWorldFileBytes();
		Message response = WorldServerMessageFactory.generateWorldFileResponse(worldFileBytes);
		getClientHandler().enqueueMessage(response);
		return null;
	}
}
