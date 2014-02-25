package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.WorldClient;
import com.jenjinstudios.world.WorldObject;

/**
 * Process an ActorVisibleMessage.
 * @author Caleb Brinkman
 */
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
	public void runSynced() {
		getClient().getWorld().addObject(newlyVisible, newlyVisible.getId());
	}

	@Override
	public void runASync() {
		Message message = getMessage();
		String name = (String) message.getArgument("name");
		int id = (int) message.getArgument("id");
		int resourceID = (int) message.getArgument("resourceID");
		double xCoordinate = (double) message.getArgument("xCoordinate");
		double yCoordinate = (double) message.getArgument("yCoordinate");

		newlyVisible = new WorldObject(name);
		newlyVisible.setId(id);
		newlyVisible.setResourceID(resourceID);
		newlyVisible.setVector2D(xCoordinate, yCoordinate);
	}
}
