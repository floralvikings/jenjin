package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles processing an ActorInvisibleMessage.
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableObjectInvisibleMessage extends WorldClientExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableObjectInvisibleMessage.class.getName());
	private int id;

	public ExecutableObjectInvisibleMessage(WorldClient client, Message message) { super(client, message); }

	@Override
	public void runDelayed() {
		LOGGER.log(Level.FINEST, "Before processing ObjectInvisibleMessage, world contains: {0}",
			  getClient().getWorld().getObjectCount());
		getClient().getWorld().removeObject(id);
		LOGGER.log(Level.FINEST, "After processing ObjectInvisibleMessage, world contains: {0}",
			  getClient().getWorld().getObjectCount());
	}

	@Override
	public void runImmediate() { id = (int) getMessage().getArgument("id"); }
}
