package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.WorldClient;
import com.jenjinstudios.world.state.MoveState;

/**
 * Process an ActorVisibleMessage.
 * @author Caleb Brinkman
 */
public class ExecutableActorVisibleMessage extends WorldClientExecutableMessage
{
	/** The newly visible actor. */
	ClientActor newlyVisible;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableActorVisibleMessage(WorldClient client, Message message) {
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
		double direction = (double) message.getArgument("relativeAngle");
		double angle = (double) message.getArgument("absoluteAngle");
		int stepsFromLast = (int) message.getArgument("stepsTaken");
		int stepsUntilChange = (int) message.getArgument("stepsUntilChange");

		newlyVisible = new ClientActor(id, name);
		newlyVisible.setResourceID(resourceID);
		newlyVisible.setVector2D(xCoordinate, yCoordinate);
		// TODO Maybe should be replaced with world update time?
		MoveState state = new MoveState(direction, stepsUntilChange, angle, newlyVisible.getVector2D(), System.nanoTime());
		newlyVisible.setCurrentMoveState(state);
		newlyVisible.setStepsTaken(stepsFromLast);
	}
}
