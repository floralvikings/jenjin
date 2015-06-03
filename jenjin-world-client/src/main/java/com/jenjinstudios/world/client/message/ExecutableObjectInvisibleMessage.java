package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClientMessageContext;

/**
 * Handles processing an ActorInvisibleMessage.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableObjectInvisibleMessage extends WorldClientExecutableMessage<WorldClientMessageContext>
{
	public ExecutableObjectInvisibleMessage(Message message, WorldClientMessageContext context) {
		super(message, context);
	}

    @Override
	public Message execute() {
		String id = (String) getMessage().getArgument("id");
		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> world.removeChildRecursively(id));
		return null;
	}
}
