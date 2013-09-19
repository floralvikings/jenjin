package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.WorldClient;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.ClientObject;

/**
 * Process an ActorVisibleMessage.
 *
 * @author Caleb Brinkman
 */
public class ExecutableObjectVisibleMessage extends WorldClientExecutableMessage
{
	/** The newly visible actor. */
	ClientObject newlyVisible;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param client  The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableObjectVisibleMessage(WorldClient client, Message message)
	{
		super(client, message);
	}

	@Override
	public void runSynced()
	{
		getClient().addNewVisible(newlyVisible);
	}

	@Override
	public void runASync()
	{
		Message message = getMessage();
		String name = (String) message.getArgument("name");
		int id = (int) message.getArgument("id");
		double xCoord = (double) message.getArgument("xCoordinate");
		double zCoord = (double) message.getArgument("zCoordinate");

		newlyVisible = new ClientActor(id, name);
		newlyVisible.setVector2D(xCoord, zCoord);
	}
}
