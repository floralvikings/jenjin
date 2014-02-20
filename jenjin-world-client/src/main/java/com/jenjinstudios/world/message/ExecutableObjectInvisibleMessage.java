package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.WorldClient;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles processing an ActorInvisibleMessage.
 * @author Caleb Brinkman
 */
public class ExecutableObjectInvisibleMessage extends WorldClientExecutableMessage
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutableObjectInvisibleMessage.class.getName());
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
		getClient().getWorld().removeObject(id);
	}

	@Override
	public void runASync() {
		id = (int) getMessage().getArgument("id");
	}
}
