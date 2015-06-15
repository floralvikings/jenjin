package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.event.NodeObserver;
import com.jenjinstudios.world.reflection.DynamicMethod;

import java.util.Objects;

/**
 * Observes a WorldObject for movement between Cells.
 *
 * @author Caleb Brinkman
 */
public class CellChangeObserver extends NodeObserver<CellChangeEvent>
{
	private Cell previousCell;

	/**
	 * Observe the given WorldObject post-update to determine whether the previos update caused the actor to move from
	 * one cell to another.
	 *
	 * @param actor The Actor to observe.
	 *
	 * @return A CellChangeEvent if the object moved from one cell to another, null otherwise.
	 */
	@DynamicMethod
	public CellChangeEvent observePostUpdate(Actor actor) {
		CellChangeEvent event = null;
		Cell currentCell = actor.getParent();
		if (!Objects.equals(previousCell, currentCell)) {
			event = new CellChangeEvent(actor, previousCell, currentCell);
			previousCell = currentCell;
		}
		return event;
	}
}
