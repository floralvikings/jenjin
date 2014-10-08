package com.jenjinstudios.world.util;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

/**
 * @author Caleb Brinkman
 */
public class ActorUtils
{

	private static boolean canStepForward(WorldObject o, double stepLength) {
		boolean canStep;
		if (o.getAngle().isNotIdle())
		{
			Vector2D newVector = o.getVector2D().getVectorInDirection(stepLength, o.getAngle().getStepAngle());
			Location newLocation = ZoneUtils.getLocationForCoordinates(o.getWorld(), o.getZoneID(), newVector);
			canStep = newLocation != null && !"false".equals(newLocation.getProperties().get("walkable"));
		} else
		{
			canStep = true;
		}
		return canStep;
	}

	private static double calcStepLength(Actor actor) {
		double lastUpdateCompleted = (double) actor.getWorld().getLastUpdateCompleted();
		double moveSpeed = actor.getMoveSpeed();
		double timePastInSeconds = (System.currentTimeMillis() - lastUpdateCompleted) / 1000;
		return timePastInSeconds * moveSpeed;
	}

	public static void stepForward(Actor actor) {
		double stepLength = calcStepLength(actor);
		if (canStepForward(actor, stepLength))
		{
			Vector2D newVector = actor.getVector2D().getVectorInDirection(stepLength, actor.getAngle().getStepAngle());
			actor.setVector2D(newVector);
		} else
		{
			forceIdle(actor);
		}
	}

	public static void forceIdle(Actor actor) {
		Angle idle = actor.getAngle().asIdle();
		Vector2D vector2D = actor.getVector2D();
		long lastUpdateCompleted = actor.getWorld().getLastUpdateCompleted();
		MoveState forcedMoveState = new MoveState(idle, vector2D, lastUpdateCompleted);
		actor.setForcedState(forcedMoveState);
		actor.setVector2D(vector2D);
		actor.setAngle(idle);
	}
}
