package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Process an ActorVisibleMessage.
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableObjectVisibleMessage extends WorldClientExecutableMessage
{
	/** The newly visible actor. */
	WorldObject newlyVisible;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableObjectVisibleMessage(WorldClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runDelayed() {
		getClient().getWorld().getWorldObjects().scheduleForAddition(newlyVisible, newlyVisible.getId());
	}

	@Override
	public void runImmediate() {
		Message message = getMessage();
		String name = (String) message.getArgument("name");
		int id = (int) message.getArgument("id");
		int resourceID = (int) message.getArgument("resourceID");
		double xCoordinate = (double) message.getArgument("xCoordinate");
		double yCoordinate = (double) message.getArgument("yCoordinate");
		Vector2D vector2D = new Vector2D(xCoordinate, yCoordinate);

		newlyVisible = new WorldObject(name);
		newlyVisible.setId(id);
		newlyVisible.setResourceID(resourceID);
		newlyVisible.setVector2D(vector2D);
	}
}
