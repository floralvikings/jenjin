package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServerMessageContext;

/**
 * Process a WorldChecksumRequest.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldChecksumRequest extends WorldExecutableMessage<WorldServerMessageContext<Player>>
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldChecksumRequest(WorldClientHandler handler, Message message,
										  WorldServerMessageContext<Player> context)
	{
		super(handler, message, context);
	}

	@Override
	public Message execute() {
		byte[] checkSum = getContext().getWorldChecksum();
		return WorldServerMessageFactory.generateWorldChecksumResponse(checkSum);
	}

}
