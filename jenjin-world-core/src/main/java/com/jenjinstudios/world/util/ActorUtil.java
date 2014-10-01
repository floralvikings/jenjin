package com.jenjinstudios.world.util;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Vector2D;

/**
 * @author Caleb Brinkman
 */
public class ActorUtil
{
	public static boolean canStepForward(WorldObject o, double stepLength) {
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

	public static double calcStepLength(Actor actor) {
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
			actor.forceIdle();
		}
	}
}
