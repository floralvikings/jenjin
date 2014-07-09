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
		if (!isCorrectionSafe(player))
		{
			player.setForcedState(new MoveState(player.getAngle(), player.getVector2D(), System.nanoTime()));
		} else
		{
			player.setAngle(angle);
			player.setVector2D(position);
			player.setLastStepTime(System.nanoTime());
		}
	}

	private boolean isCorrectionSafe(Actor player) {
		Vector2D proposedPlayerOrigin = getPlayerOrigin(player);
		Vector2D proposedClientOrigin = getClientOrigin();
		return proposedClientOrigin.equals(proposedPlayerOrigin);
	}

	private Vector2D getClientOrigin() {
		double clientReverseAngle = angle.reverseStepAngle();
		return position.getVectorInDirection(distance, clientReverseAngle);
	}

	private Vector2D getPlayerOrigin(Actor player) {
		double originDistance = player.getVector2D().getDistanceToVector(uncorrectedPosition);
		double playerReverseAngle = player.getAngle().reverseStepAngle();
		return player.getVector2D().getVectorInDirection(originDistance, playerReverseAngle);
	}

	@Override
	public void runImmediate() {
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
		double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		double x = (double) getMessage().getArgument("xCoordinate");
		double y = (double) getMessage().getArgument("yCoordinate");
		long timeOfChange = (long) getMessage().getArgument("timeOfChange");
		uncorrectedPosition = new Vector2D(x, y);
		angle = new Angle(absoluteAngle, relativeAngle);
		distance = ClientActor.MOVE_SPEED * ((double) (System.nanoTime() - timeOfChange) / 1000000000d);
		position = uncorrectedPosition.getVectorInDirection(distance, angle.getStepAngle());
	}
}
