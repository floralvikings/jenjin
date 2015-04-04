package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.net.ClientMessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;

/**
 * Handles processing an ActorInvisibleMessage.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableObjectInvisibleMessage extends WorldClientExecutableMessage<ClientMessageContext>
{
	public ExecutableObjectInvisibleMessage(WorldClient client, Message message, ClientMessageContext context) {
		super
		  (client, message, context); }

    @Override
	public Message execute() {
		int id = (int) getMessage().getArgument("id");
		World world = getWorldClient().getWorld();
		world.scheduleUpdateTask(() -> world.getWorldObjects().remove(id));
		return null;
	}
}
