package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.ClientObject;
import com.jenjinstudios.world.WorldClient;

/**
 * Process an ActorVisibleMessage.
 * @author Caleb Brinkman
 */
public class ExecutableObjectVisibleMessage extends WorldClientExecutableMessage
{
	/** The newly visible actor. */
	ClientObject newlyVisible;

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
		getClient().getPlayer().addNewVisible(newlyVisible);
	}

	@Override
	public void runASync() {
		Message message = getMessage();
		String name = (String) message.getArgument("name");
		int id = (int) message.getArgument("id");
		double xCoord = (double) message.getArgument("xCoordinate");
		double yCoord = (double) message.getArgument("yCoordinate");

		newlyVisible = new ClientObject(id, name);
		newlyVisible.setVector2D(xCoord, yCoord);
	}
}
