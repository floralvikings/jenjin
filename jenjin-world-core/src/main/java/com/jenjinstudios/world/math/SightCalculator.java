package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Cell;
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
	 * @return A collection of objects visible to the actor.
	 */
	Collection<WorldObject> getVisibleObjects();

	/**
	 * Get all cells visible to this SightCalculator's actor.
	 *
	 * @return A collection of cells visible to the Actor.
	 */
	Collection<Cell> getVisibleCells();
}
