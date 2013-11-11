package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.message.Message;
import com.jenjinstudios.net.WorldClient;
import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.ClientObject;
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
		ClientObject obj = getClient().getObject(actorID);
		if (obj != null && obj instanceof ClientActor)
		{
			ClientActor actor = (ClientActor) obj;
			actor.addMoveState(newState);
		}
	}

	@Override
	public void runASync() {
		actorID = (int) getMessage().getArgument("id");
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
		double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		int stepsUntilChange = (int) getMessage().getArgument("stepsUntilChange");
		newState = new MoveState(relativeAngle, stepsUntilChange, absoluteAngle);
	}
}
