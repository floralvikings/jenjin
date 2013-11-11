package com.jenjinstudios.world.message;

import com.jenjinstudios.jgsf.WorldClientHandler;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.state.MoveState;

/**
 * Process a StateChangeRequest.
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeRequest extends WorldExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableStateChangeRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
	}

	@Override
	public void runSynced() {
		Actor player = getClientHandler().getActor();
		MoveState newState;
		double direction = (double) getMessage().getArgument("relativeAngle");
		double angle = (double) getMessage().getArgument("absoluteAngle");
		int stepsFromLast = (int) getMessage().getArgument("stepsUntilChange");
		newState = new MoveState(direction, stepsFromLast, angle);
		player.addMoveState(newState);
	}

	@Override
	public void runASync() {
	}
}
