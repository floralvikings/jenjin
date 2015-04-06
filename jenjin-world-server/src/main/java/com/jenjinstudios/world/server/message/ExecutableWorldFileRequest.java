package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ServerMessageContext;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;

/**
 * Process a WorldChecksumRequest.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldFileRequest extends WorldExecutableMessage<ServerMessageContext>
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldFileRequest(WorldClientHandler handler, Message message, ServerMessageContext context) {
		super(handler, message, context);
	}

	@Override
	public Message execute() {
		byte[] worldFileBytes = ((WorldServer) getClientHandler().getServer()).getWorldFileBytes();
		return WorldServerMessageFactory.generateWorldFileResponse(worldFileBytes);
	}
}
