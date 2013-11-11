package com.jenjinstudios.message;

import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.WorldClient;

/**
 * Sets the ClientActor step length.
 * @author Caleb Brinkman
 */
public class ExecutableActorStepLengthMessage extends WorldClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableActorStepLengthMessage(WorldClient client, Message message) {
		super(client, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runSynced() {
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runASync() {
		ClientActor.STEP_LENGTH = (double) getMessage().getArgument("stepLength");
	}
}
