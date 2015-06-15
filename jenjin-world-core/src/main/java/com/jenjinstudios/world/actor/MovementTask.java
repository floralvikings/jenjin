package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Orientation;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.object.Timing;
import com.jenjinstudios.world.reflection.DynamicMethod;
import com.jenjinstudios.world.task.NodeTask;

import static com.jenjinstudios.world.math.Orientation.NOWHERE;

/**
 * Causes an actor to during an update.
 *
 * @author Caleb Brinkman
 */
public class MovementTask extends NodeTask
{
	private static final double MILLIS_PER_SECOND = 1000.0;

	/**
	 * Move the Actor forward according to its movement speed and the time past since the last time it was updated.
	 *
	 * @param actor The actor to move forward.
	 */
	@DynamicMethod
	public void onUpdate(Actor actor) {
		if (!actor.getMovement().getOrientation().equals(NOWHERE)) {
			Vector newVector = getNewVector(actor);
			Zone zone = actor.getParent().getParent();

			if (actor.getParent().equals(zone.getCell(newVector))) {
				actor.getGeometry().setPosition(newVector);
			} else {
				moveBetweenCells(actor, newVector);
			}
		}
	}

	private static void moveBetweenCells(Actor actor, Vector newVector) {
		Cell oldCell = actor.getParent();
		Zone zone = oldCell.getParent();
		Cell newCell = zone.getCell(newVector);

		if (zone.areAdjacent(oldCell, newCell)) {
			oldCell.removeChild(actor);
			newCell.addChild(actor);
			actor.getGeometry().setPosition(newVector);
		} else {
			// Stop the actor from making an illegal move.
			actor.getMovement().setOrientation(NOWHERE);
		}
	}

	private static Vector getNewVector(Actor actor) {
		Timing timing = actor.getTiming();
		long millisPast = timing.getLastUpdateStartTime() - timing.getLastUpdateEndTime();
		double secondsPast = millisPast / MILLIS_PER_SECOND;
		double distance = secondsPast * actor.getMovement().getSpeed();

		Orientation rel = actor.getMovement().getOrientation();
		Orientation abs = actor.getGeometry().getOrientation();
		Orientation combined = new Orientation(abs.getYaw() + rel.getYaw(), abs.getPitch() + rel.getPitch());

		return actor.getGeometry().getPosition().getVectorInDirection(distance, combined);
	}
}
