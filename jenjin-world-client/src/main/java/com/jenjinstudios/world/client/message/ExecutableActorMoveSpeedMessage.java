package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientMessageContext;

/**
 * Sets the ClientActor step length.
 * @author Caleb Brinkman
 */
public class ExecutableActorMoveSpeedMessage extends WorldClientExecutableMessage<WorldClientMessageContext>
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableActorMoveSpeedMessage(WorldClient client, Message message, WorldClientMessageContext context) {
		super(client, message, context);
	}

	/** Run asynchronous portion of this message. */
	@Override
	public Message execute() {
		Actor.DEFAULT_MOVE_SPEED = (double) getMessage().getArgument("moveSpeed");
		return null;
	}
}
