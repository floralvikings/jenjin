package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.WorldClient;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;
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
		double relativeAngle = (double) message.getArgument("relativeAngle");
		double absoluteAngle = (double) message.getArgument("absoluteAngle");
		int stepsFromLast = (int) message.getArgument("stepsTaken");
		int stepsUntilChange = (int) message.getArgument("stepsUntilChange");
		long timeOfVisibility = (long) message.getArgument("timeOfVisibility");

		newlyVisible = new ClientActor(id, name);
		newlyVisible.setResourceID(resourceID);
		double dist = ClientActor.MOVE_SPEED *
				((System.nanoTime() - timeOfVisibility) / 1000000000);
		double angle = MathUtil.calcStepAngle(absoluteAngle, relativeAngle);
		Vector2D oldVector = new Vector2D(xCoordinate, yCoordinate);
		Vector2D newVector = oldVector.getVectorInDirection(dist, angle);
		newlyVisible.setVector2D(newVector);
		newlyVisible.setAbsoluteAngle(absoluteAngle);
		newlyVisible.setRelativeAngle(relativeAngle);
	}
}
