package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;

/**
 * Handles processing an ActorInvisibleMessage.
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableObjectInvisibleMessage extends WorldClientExecutableMessage
{
	private int id;

	public ExecutableObjectInvisibleMessage(WorldClient client, Message message) { super(client, message); }

	@Override
	public void runDelayed() {
		getClient().getWorld().getWorldObjects().remove(id);
	}

	@Override
	public void runImmediate() { id = (int) getMessage().getArgument("id"); }
}
