package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;

/**
 * Handles processing an ActorInvisibleMessage.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableObjectInvisibleMessage extends WorldClientExecutableMessage
{
    private int id;

    public ExecutableObjectInvisibleMessage(WorldClient client, Message message) { super(client, message); }

    @Override
    public void runDelayed() {
    }

    @Override
	public void runImmediate() {
		id = (int) getMessage().getArgument("id");
		World world = getConnection().getWorld();
		world.scheduleUpdateTask(() -> world.getWorldObjects().remove(id));
	}
}
