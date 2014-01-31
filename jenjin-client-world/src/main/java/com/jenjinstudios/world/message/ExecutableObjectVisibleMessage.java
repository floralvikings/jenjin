package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.InvalidLocationException;
import com.jenjinstudios.world.WorldClient;
import com.jenjinstudios.world.WorldObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Process an ActorVisibleMessage.
 * @author Caleb Brinkman
 */
public class ExecutableObjectVisibleMessage extends WorldClientExecutableMessage
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutableObjectVisibleMessage.class.getName());
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
		try
		{
			getClient().getWorld().addObject(newlyVisible, newlyVisible.getId());
		} catch (InvalidLocationException e)
		{
			LOGGER.log(Level.INFO, "Tried to place newly visible actor in invalid location.");
		}
	}

	@Override
	public void runASync() {
		Message message = getMessage();
		String name = (String) message.getArgument("name");
		int id = (int) message.getArgument("id");
		double xCoord = (double) message.getArgument("xCoordinate");
		double yCoord = (double) message.getArgument("yCoordinate");

		newlyVisible = new WorldObject(name);
		newlyVisible.setId(id);
		newlyVisible.setVector2D(xCoord, yCoord);
	}
}
