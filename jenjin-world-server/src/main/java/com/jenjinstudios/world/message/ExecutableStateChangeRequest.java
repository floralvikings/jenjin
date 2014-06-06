package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.WorldClientHandler;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Process a StateChangeRequest.
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeRequest extends WorldExecutableMessage
{
	public static final double MAX_CORRECT_DISTANCE = 2.0;
	private double relativeAngle;
	private double absoluteAngle;
	private Vector2D position;
	private double distance;

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
		if(distance > MAX_CORRECT_DISTANCE) {
			// TODO Force player state here.
			return;
		}
		player.setRelativeAngle(relativeAngle);
		player.setAbsoluteAngle(absoluteAngle);
		player.setVector2D(position);
		player.setLastStepTime(System.nanoTime());
	}

	@Override
	public void runASync() {
		relativeAngle = (double) getMessage().getArgument("relativeAngle");
		absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		long time = (long) getMessage().getArgument("timeOfChange");
		double x = (double) getMessage().getArgument("xCoord");
		double y = (double) getMessage().getArgument("yCoord");
		Vector2D oldPosition = new Vector2D(x,y);
		double angle = MathUtil.calcStepAngle(absoluteAngle, relativeAngle);
		distance = ClientActor.MOVE_SPEED * ((double)(System.nanoTime() - time) / 1000000000d);
		position = oldPosition.getVectorInDirection(distance, angle);
	}
}
