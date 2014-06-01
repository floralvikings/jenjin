package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldClientHandler;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

/**
 * Process a StateChangeRequest.
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeRequest extends WorldExecutableMessage
{
	private double direction;
	private int stepsFromLast;
	private double angle;
	private Vector2D position;
	private long time;

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
		Actor player = getClientHandler().getPlayer();
		// TODO Perform position correction here; will also need some verification.
		MoveState newState = new MoveState(direction, stepsFromLast, angle, position, time);
		player.addMoveState(newState);
	}

	@Override
	public void runASync() {
		direction = (double) getMessage().getArgument("relativeAngle");
		angle = (double) getMessage().getArgument("absoluteAngle");
		stepsFromLast = (int) getMessage().getArgument("stepsUntilChange");
		time = (long) getMessage().getArgument("timeOfChange");
		double x = (double) getMessage().getArgument("xCoord");
		double y = (double) getMessage().getArgument("yCoord");
		position = new Vector2D(x,y);
	}
}
