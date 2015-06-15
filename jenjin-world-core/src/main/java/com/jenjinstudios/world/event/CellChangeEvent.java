package com.jenjinstudios.world.event;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.actor.Actor;

/**
 * Represents an actor changing cells from one update to the next.
 *
 * @author Caleb Brinkman
 */
public class CellChangeEvent implements NodeEvent
{
	private final Actor actor;
	private final Cell previous;
	private final Cell current;

	/**
	 * Construct a new CellChangeEvent caused by the given WorldObject moving from the given previous cell to the given
	 * current cell.
	 *
	 * @param actor The actor which has moved between cells.
	 * @param previous The Cell in which the actor was previously located.
	 * @param current The Cell in which the actor is currently located.
	 */
	public CellChangeEvent(Actor actor, Cell previous, Cell current) {
		this.actor = actor;
		this.previous = previous;
		this.current = current;
	}

	/**
	 * Get the actor which has changed cells.
	 *
	 * @return The actor which has changed cells.
	 */
	public Actor getActor() { return actor; }

	/**
	 * Get the cell previously occupied by the actor.
	 *
	 * @return The previously occupied cell.
	 */
	public Cell getPrevious() { return previous; }

	/**
	 * Get the cell now occupied by the actor.
	 *
	 * @return The cell now occupied by the actor.
	 */
	public Cell getCurrent() { return current; }
}
