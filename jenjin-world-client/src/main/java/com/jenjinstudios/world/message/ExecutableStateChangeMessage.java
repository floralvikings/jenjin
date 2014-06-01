package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.WorldClient;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

/**
 * Process a StateChangeMessage.
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeMessage extends WorldClientExecutableMessage
{
	/** The new state for the actor. */
	private MoveState newState;
	/** The ID of the actor to which to add the state. */
	private int actorID;
	private double relativeAngle;
	private double absoluteAngle;
	private int stepsUntilChange;
	private Vector2D position;
	private long time;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableStateChangeMessage(WorldClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runSynced() {
		WorldObject obj = getClient().getWorld().getObject(actorID);
		if (obj != null && obj instanceof ClientActor)
		{
			ClientActor actor = (ClientActor) obj;
			// TODO Perform position correction here, based on time, position, and new angles.
			newState = new MoveState(relativeAngle, stepsUntilChange, absoluteAngle, position, time);
			actor.addMoveState(newState);
		}
	}

	@Override
	public void runASync() {
		actorID = (int) getMessage().getArgument("id");
		relativeAngle = (double) getMessage().getArgument("relativeAngle");
		absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		stepsUntilChange = (int) getMessage().getArgument("stepsUntilChange");
		time = (long) getMessage().getArgument("timeOfChange");
		double x = (double) getMessage().getArgument("xCoord");
		double y = (double) getMessage().getArgument("yCoord");
		position = new Vector2D(x,y);
	}
}
