package com.jenjinstudios.world.task;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.math.Orientation;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.reflection.DynamicMethod;

/**
 * Causes an actor to during an update.
 *
 * @author Caleb Brinkman
 */
public class MovementTask extends NodeTask
{
	/**
	 * Move the Actor forward according to its movement speed and the time past since the last time it was updated.
	 *
	 * @param actor The actor to move forward.
	 */
	@DynamicMethod
	public void onUpdate(Actor actor) {
		long millisPast = actor.getTiming().getLastUpdateStartTime() - actor.getTiming().getLastUpdateEndTime();
		double distance = (millisPast * actor.getMoveSpeed()) / 1000;
		Vector oldVector = actor.getGeometry().getPosition();
		Orientation orientation = actor.getGeometry().getOrientation();
		Vector newVector = oldVector.getVectorInDirection(distance, orientation);

		Cell oldCell = actor.getParent();
		if (oldCell.containsVector(newVector)) {
			actor.getGeometry().setPosition(newVector);
		} else {
			Cell newCell = oldCell.getParent().getCell(newVector);
			if ((newCell != null) && oldCell.isAdjacentTo(newCell)) {
				oldCell.removeChild(actor);
				newCell.addChild(actor);
				actor.getGeometry().setPosition(newVector);
			}
		}
	}
}
