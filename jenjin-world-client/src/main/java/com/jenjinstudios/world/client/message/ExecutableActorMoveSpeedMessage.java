package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.client.WorldClient;

/**
 * Sets the ClientActor step length.
 * @author Caleb Brinkman
 */
public class ExecutableActorMoveSpeedMessage extends WorldClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableActorMoveSpeedMessage(WorldClient client, Message message) {
		super(client, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runDelayed() {
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runImmediate() {
		Actor.DEFAULT_MOVE_SPEED = (double) getMessage().getArgument("moveSpeed");
	}
}
