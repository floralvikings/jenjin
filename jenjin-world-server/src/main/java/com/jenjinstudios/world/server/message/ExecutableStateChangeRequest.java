package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.client.ClientActor;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

/**
 * Process a StateChangeRequest.
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableStateChangeRequest extends WorldExecutableMessage
{
	/** The maximum allowable correction distance. */
	public static final double MAX_CORRECT_DISTANCE = 2.0;
	private Angle angle;
	/** The new position, corrected for lag. */
	private Vector2D position;
	/** The distance from the received position to the new position. */
	private double distance;
	/** The position before correction. */
	private Vector2D uncorrectedPosition;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableStateChangeRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
	}

	@Override
	public void runDelayed() {
		Actor player = getClientHandler().getPlayer();
		double originDistance = player.getVector2D().getDistanceToVector(uncorrectedPosition);
		if (originDistance > MAX_CORRECT_DISTANCE || distance > MAX_CORRECT_DISTANCE)
		{
			// What if player hacks their jar and increases the move speed to be the max correct, then spoofs
			// state change requests of the same direction over and over?  They've effectively just
			// increased their speed by 10x.  This requires some thought.
			// TODO Force player state here.
			player.setForcedState(new MoveState(player.getAngle(), player.getVector2D(), System.nanoTime()));
		} else
		{
			player.setAngle(angle);
			player.setVector2D(position);
			player.setLastStepTime(System.nanoTime());
		}
	}

	@Override
	public void runImmediate() {
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
		double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		long time = (long) getMessage().getArgument("timeOfChange");
		double x = (double) getMessage().getArgument("xCoordinate");
		double y = (double) getMessage().getArgument("yCoordinate");
		uncorrectedPosition = new Vector2D(x, y);
		angle = new Angle(absoluteAngle, relativeAngle);
		distance = ClientActor.MOVE_SPEED * ((double) (System.nanoTime() - time) / 1000000000d);
		position = uncorrectedPosition.getVectorInDirection(distance, angle.getStepAngle());
	}
}
