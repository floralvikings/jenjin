package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.actor.Actor;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;

/**
 * Interface that should be implemented for Line-Of-Sight calculations.
 *
 * @author Caleb Brinkman
 */
public interface SightCalculator
{
	/**
	 * Get all objects Visible to the specified Actor.
	 *
	 * @param actor The actor for which to calculate visible objects.
	 *
	 * @return A collection of objects visible to the actor.
	 */
	Collection<WorldObject> getVisibleObjects(Actor actor);

	/**
	 * Get all cells visible to this SightCalculator's actor.
	 *
	 * @param actor The actor for which to calculate visible cells.
	 *
	 * @return A collection of cells visible to the Actor.
	 */
	Collection<Cell> getVisibleCells(Actor actor);
}
