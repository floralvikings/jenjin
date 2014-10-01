package com.jenjinstudios.world.util;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.math.Vector2D;

/**
 * @author Caleb Brinkman
 */
public class ActorUtil
{
	public static boolean canStepForward(Actor actor, double stepLength) {
		boolean canStep;
		if (actor.getAngle().isNotIdle())
		{
			Vector2D newVector = actor.getVector2D().getVectorInDirection(stepLength, actor.getAngle().getStepAngle());
			Location newLocation = ZoneUtils.getLocationForCoordinates(actor.getWorld(), actor.getZoneID(), newVector);
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
}
