package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.server.WorldServerMessageContext;

/**
 * Process a WorldChecksumRequest.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldChecksumRequest extends WorldExecutableMessage<WorldServerMessageContext>
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *  @param message The message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldChecksumRequest(Message message, WorldServerMessageContext context)
	{
		super(message, context);
	}

	@Override
	public Message execute() {
		byte[] checkSum = getContext().getWorldChecksum();
		return WorldServerMessageFactory.generateWorldChecksumResponse(checkSum);
	}

}
