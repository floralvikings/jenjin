package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Process an ActorVisibleMessage.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableObjectVisibleMessage extends WorldClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
     */
    public ExecutableObjectVisibleMessage(WorldClient client, Message message) {
        super(client, message);
    }

    @Override
	public Message execute() {
		Message message = getMessage();
        String name = (String) message.getArgument("name");
        int id = (int) message.getArgument("id");
        int resourceID = (int) message.getArgument("resourceID");
        double xCoordinate = (double) message.getArgument("xCoordinate");
        double yCoordinate = (double) message.getArgument("yCoordinate");
        Vector2D vector2D = new Vector2D(xCoordinate, yCoordinate);

		WorldObject newlyVisible = new WorldObject(name);
		newlyVisible.setId(id);
        newlyVisible.setResourceID(resourceID);
        newlyVisible.setVector2D(vector2D);

		World world = getWorldClient().getWorld();
		world.scheduleUpdateTask(() -> {
			world.getWorldObjects().set(newlyVisible.getId(), newlyVisible);
		});
		return null;
	}
}
