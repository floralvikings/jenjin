package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;

/**
 * Handles processing an ActorInvisibleMessage.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableObjectInvisibleMessage extends WorldClientExecutableMessage<MessageContext>
{
	public ExecutableObjectInvisibleMessage(WorldClient client, Message message, MessageContext context) { super
		  (client, message, context); }

    @Override
	public Message execute() {
		int id = (int) getMessage().getArgument("id");
		World world = getWorldClient().getWorld();
		world.scheduleUpdateTask(() -> world.getWorldObjects().remove(id));
		return null;
	}
}
