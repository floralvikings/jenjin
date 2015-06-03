package com.jenjinstudios.world.util;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.object.WorldObject;

/**
 * @author Caleb Brinkman
 */
public class ActorUtils
{

	/**
	 * Move the given object one "step length" forward; this ensures that an object moves at the same speed regardless
	 * of the time between updates.
	 *
	 * @param object The object to be moved.
	 */
	public static void stepForward(WorldObject object) {
		double stepLength = calcStepLength(object);
		if (canStepForward(object, stepLength)) {
			Vector position = object.getGeometry().getPosition();
			double stepAngle = object.getGeometry().getOrientation().getStepAngle();
			Vector newVector = position.getVectorInDirection(stepLength, stepAngle);
			object.getGeometry().setPosition(newVector);
		} else {
			forceIdle(object);
		}
	}

	public static void forceIdle(WorldObject object) {
		Angle idle = object.getGeometry().getOrientation().asIdle();
		Vector vector = object.getGeometry().getPosition();
		object.getGeometry().setPosition(vector);
		object.getGeometry().setOrientation(idle);
	}

	private static boolean canStepForward(WorldObject object, double stepLength) {
		Vector position = object.getGeometry().getPosition();
		double stepAngle = object.getGeometry().getOrientation().getStepAngle();
		Vector newVector = position.getVectorInDirection(stepLength, stepAngle);
		Cell newCell = object.getParent().getParent().getCell(newVector);
		return (newCell != null) && !"false".equals(newCell.getProperty("walkable"));
	}

	private static double calcStepLength(WorldObject object) {
		double lastUpdateCompleted = object.getTiming().getLastUpdateEndTime();
		double moveSpeed = object.getGeometry().getSpeed();
		double timePastInSeconds = (System.currentTimeMillis() - lastUpdateCompleted) / 1000;
		return timePastInSeconds * moveSpeed;
	}
}
