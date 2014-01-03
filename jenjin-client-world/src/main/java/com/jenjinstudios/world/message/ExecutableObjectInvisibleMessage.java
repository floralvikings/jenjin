package com.jenjinstudios.world.message;

import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.WorldClient;

/**
 * Handles processing an ActorInvisibleMessage.
 * @author Caleb Brinkman
 */
public class ExecutableObjectInvisibleMessage extends WorldClientExecutableMessage
{
	/** The ID of the object to be made invisible. */
	private int id;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableObjectInvisibleMessage(WorldClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runSynced() {
		getClient().removeVisible(id);
	}

	@Override
	public void runASync() {
		id = (int) getMessage().getArgument("id");
	}
}
