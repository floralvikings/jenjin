package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientMessageContext;

/**
 * Handles processing an ActorInvisibleMessage.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableObjectInvisibleMessage extends WorldClientExecutableMessage<WorldClientMessageContext>
{
	public ExecutableObjectInvisibleMessage(WorldClient client, Message message, WorldClientMessageContext context) {
		super(client, message, context);
	}

    @Override
	public Message execute() {
		int id = (int) getMessage().getArgument("id");
		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> world.getWorldObjects().remove(id));
		return null;
	}
}
