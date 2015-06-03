package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClientMessageContext;

/**
 * Sets the ClientActor step length.
 * @author Caleb Brinkman
 */
public class ExecutableActorMoveSpeedMessage extends WorldClientExecutableMessage<WorldClientMessageContext>
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableActorMoveSpeedMessage(Message message, WorldClientMessageContext context) {
		super(message, context);
	}

	/** Run asynchronous portion of this message. */
	@Override
	public Message execute() {
		// TODO Should probably implement this instead of just ignoring it.
		return null;
	}
}
