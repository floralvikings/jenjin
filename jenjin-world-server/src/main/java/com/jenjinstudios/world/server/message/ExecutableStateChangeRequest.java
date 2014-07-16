package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.state.MoveState;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Process a StateChangeRequest.
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableStateChangeRequest extends WorldExecutableMessage
{
	private static final double MAX_CORRECT = Actor.MOVE_SPEED;
	private static final Logger LOGGER = Logger.getLogger(ExecutableStateChangeRequest.class.getName());
	private Angle angle;
	/** The new position, corrected for lag. */
	private Vector2D position;
	/** The distance from the received position to the new position. */
	private double distance;
	/** The position before correction. */
	private Vector2D uncorrectedPosition;
	private long timePast;
	private long timeOfChange;

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
		Player player = getClientHandler().getPlayer();
		if (!isCorrectionSafe(player))
		{
			player.setForcedState(new MoveState(player.getAngle(), player.getVectorBeforeUpdate(),
				  System.currentTimeMillis()));
		} else
		{
			player.setPendingAngle(angle);
			player.setAngle(angle);
			player.setVector2D(position);
			player.setLastStepTime(System.currentTimeMillis());
		}
	}

	@Override
	public void runImmediate() {
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
		double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		double x = (double) getMessage().getArgument("xCoordinate");
		double y = (double) getMessage().getArgument("yCoordinate");
		timeOfChange = (long) getMessage().getArgument("timeOfChange");
		uncorrectedPosition = new Vector2D(x, y);
		angle = new Angle(absoluteAngle, relativeAngle);
		timePast = (System.currentTimeMillis() - timeOfChange);
		distance = MathUtil.round(Actor.MOVE_SPEED * ((double) timePast / 1000d), 2);
		position = uncorrectedPosition.getVectorInDirection(distance, angle.getStepAngle());
	}

	private boolean isCorrectionSafe(Player player) {
		double tolerance = Actor.MOVE_SPEED * 0.1;
		Vector2D proposedPlayerOrigin = getPlayerOrigin(player);
		double distance = uncorrectedPosition.getDistanceToVector(proposedPlayerOrigin);
		boolean distanceWithinTolerance = distance < tolerance;
		if (!distanceWithinTolerance)
		{
			LOGGER.log(Level.FINEST, "Distance to origin oustide of tolerance: {0},{1}",
				  new Object[]{distance, tolerance});
		}
		double clientDistance = uncorrectedPosition.getDistanceToVector(position);
		boolean withinMaxCorrect = clientDistance < MAX_CORRECT;
		if (!withinMaxCorrect)
		{
			LOGGER.log(Level.FINEST, "Distance to correct oustide of tolerance. " +
						"Position: {0}, Corrected: {1}, Step Angle: {2}, Time: {3}, TimePast: {4}",
				  new Object[]{uncorrectedPosition, position, angle, timeOfChange, timePast});
		}
		// Tolerance of a single update to account for timing discrepency.
		return withinMaxCorrect && distanceWithinTolerance;
	}

	private Vector2D getPlayerOrigin(Player player) {
		double originDistance = player.getVector2D().getDistanceToVector(uncorrectedPosition);
		double playerReverseAngle = player.getPendingAngle().reverseStepAngle();
		return player.getVector2D().getVectorInDirection(originDistance, playerReverseAngle);
	}
}
