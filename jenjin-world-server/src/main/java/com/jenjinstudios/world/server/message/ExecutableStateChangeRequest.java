package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldServerMessageContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Process a StateChangeRequest.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableStateChangeRequest extends WorldExecutableMessage<WorldServerMessageContext<Player>>
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableStateChangeRequest.class.getName());
	private static final double MS_TO_S = 1000.0d;
	private Angle angle;
	/** The new position, corrected for lag. */
	private Vector2D position;
	/** The position before correction. */
	private Vector2D uncorrectedPosition;
	private long timePast;
	private long timeOfChange;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *  @param message The message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableStateChangeRequest(Message message, WorldServerMessageContext<Player> context)
	{
		super(message, context);
	}

	@Override
	public Message execute() {
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
		double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		double x = (double) getMessage().getArgument("xCoordinate");
		double y = (double) getMessage().getArgument("yCoordinate");
		timeOfChange = (long) getMessage().getArgument("timeOfChange");
		uncorrectedPosition = new Vector2D(x, y);
		angle = new Angle(absoluteAngle, relativeAngle);
		timePast = (System.currentTimeMillis() - timeOfChange);

		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			Player player = getContext().getUser();
			if ((player != null) && (world != null)) {
				double distance = MathUtil.round(
					  player.getGeometry().getSpeed() * (timePast / MS_TO_S),
					  2);
				position = uncorrectedPosition.getVectorInDirection(
					  distance,
					  angle.getStepAngle());
				if (!locationWalkable(player)) {
					LOGGER.log(Level.INFO, "Attempted move to unwalkable " +
						  "location: {0}", position);
					Angle pAngle = player.getGeometry().getOrientation().asIdle();
					player.getGeometry().setOrientation(pAngle);
				} else if (!isCorrectionSafe(player)) {
					Angle pAngle = player.getGeometry().getOrientation();
					player.getGeometry().setOrientation(pAngle);
				} else {
					player.getGeometry().setOrientation(angle);
					player.getGeometry().setPosition(position);
				}
			}
		});
		return null;
	}

	private boolean locationWalkable(Player player) {
		Cell cell = player.getParent();
		boolean walkable = false;
		if (cell != null)
		{
			String prop = cell.getProperty("walkable");
			walkable = !"false".equals(prop);
		}
		return walkable;
	}

	private boolean isCorrectionSafe(Player player) {
		// Tolerance of a single update to account for timing discrepency.
		return isDistanceWithinTolerance(player) && isWithinMaxCorrect(player);

	}

	private boolean isWithinMaxCorrect(Player player) {
		double clientDistance = uncorrectedPosition.getDistanceToVector(position);
		double maxCorrect = player.getGeometry().getSpeed();
		boolean withinMaxCorrect = clientDistance < maxCorrect;
		if (!withinMaxCorrect)
		{
			LOGGER.log(Level.INFO, "Distance to correct oustide of tolerance. " +
						"Position: {0}, Corrected: {1}, Distance: {5}, Step Angle: {2}, Time: {3}, TimePast: {4}",
				  new Object[]{uncorrectedPosition, position, angle, timeOfChange, timePast, clientDistance});
		}
		return withinMaxCorrect;
	}

	private boolean isDistanceWithinTolerance(Player player) {
		double tolerance = player.getGeometry().getSpeed() / 10; // Allows for 100ms lag.
		Vector proposedPlayerOrigin = getPlayerOrigin(player);
		double distance = uncorrectedPosition.getDistanceToVector(proposedPlayerOrigin);
		boolean withinTolerance = distance < tolerance;
		if (!withinTolerance)
		{
			LOGGER.log(Level.INFO, "Distance to origin oustide of defined tolerance. Distance: {0}, Tolerance: {1}",
				  new Object[]{distance, tolerance});
		}
		return withinTolerance;
	}

	private Vector getPlayerOrigin(Player player) {
		double originDistance = player.getGeometry().getPosition().getDistanceToVector(uncorrectedPosition);
		double playerReverseAngle = angle.reverseStepAngle();
		return player.getGeometry().getPosition().getVectorInDirection(originDistance, playerReverseAngle);
	}
}
