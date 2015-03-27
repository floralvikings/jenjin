package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.state.MoveState;
import com.jenjinstudios.world.util.ZoneUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Process a StateChangeRequest.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
public class ExecutableStateChangeRequest extends WorldExecutableMessage
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
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableStateChangeRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
	}

	private void forcePlayerToAngle(Actor player, Angle pAngle) {
		Vector2D vector2D = player.getVector2D();
		MoveState forcedState = new MoveState(pAngle, vector2D, player.getWorld().getLastUpdateCompleted());
		player.setForcedState(forcedState);
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

		getClientHandler().getUser().getWorld().scheduleUpdateTask(() -> {
			Actor player = getClientHandler().getUser();
			if ((player != null) && (player.getWorld() != null))
			{
				double distance = MathUtil.round(player.getMoveSpeed() * (timePast / MS_TO_S), 2);
				position = uncorrectedPosition.getVectorInDirection(distance, angle.getStepAngle());
				if (!locationWalkable(player))
				{
					LOGGER.log(Level.INFO, "Attempted move to unwalkable location: {0}", position);
					Angle pAngle = player.getAngle().asIdle();
					forcePlayerToAngle(player, pAngle);
				} else if (!isCorrectionSafe(player))
				{
					Angle pAngle = player.getAngle();
					forcePlayerToAngle(player, pAngle);
				} else
				{
					player.setAngle(angle);
					player.setVector2D(position);
				}
			}
		});

	}

	private boolean locationWalkable(Actor player) {
		World world = player.getWorld();
		int zoneID = player.getZoneID();
		Location location = ZoneUtils.getLocationForCoordinates(world, zoneID, position);
		boolean walkable = false;
		if (location != null)
		{
			String prop = location.getProperties().get("walkable");
			walkable = !"false".equals(prop);
		}
		return walkable;
	}

	private boolean isCorrectionSafe(Actor player) {
		// Tolerance of a single update to account for timing discrepency.
		return isDistanceWithinTolerance(player) && isWithinMaxCorrect(player);

	}

	private boolean isWithinMaxCorrect(Actor player) {
		double clientDistance = uncorrectedPosition.getDistanceToVector(position);
		double maxCorrect = player.getMoveSpeed();
		boolean withinMaxCorrect = clientDistance < maxCorrect;
		if (!withinMaxCorrect)
		{
			LOGGER.log(Level.INFO, "Distance to correct oustide of tolerance. " +
						"Position: {0}, Corrected: {1}, Distance: {5}, Step Angle: {2}, Time: {3}, TimePast: {4}",
				  new Object[]{uncorrectedPosition, position, angle, timeOfChange, timePast, clientDistance});
		}
		return withinMaxCorrect;
	}

	private boolean isDistanceWithinTolerance(Actor player) {
		double tolerance = player.getMoveSpeed() / 10; // Allows for 100ms lag.
		Vector2D proposedPlayerOrigin = getPlayerOrigin(player);
		double distance = uncorrectedPosition.getDistanceToVector(proposedPlayerOrigin);
		boolean distanceWithinTolerance = distance < tolerance;
		if (!distanceWithinTolerance)
		{
			LOGGER.log(Level.INFO, "Distance to origin oustide of defined tolerance. Distance: {0}, Tolerance: {1}",
				  new Object[]{distance, tolerance});
		}
		return distanceWithinTolerance;
	}

	private Vector2D getPlayerOrigin(Actor player) {
		double originDistance = player.getVector2D().getDistanceToVector(uncorrectedPosition);
		double playerReverseAngle = angle.reverseStepAngle();
		return player.getVector2D().getVectorInDirection(originDistance, playerReverseAngle);
	}
}
