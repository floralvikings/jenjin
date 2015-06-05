package com.jenjinstudios.world.event;

import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Contains the object which is seeing the newly visible objects, and a
 * collection of those objects.
 *
 * @author Caleb Brinkman
 */
public class NewlyVisibleEvent implements NodeEvent
{
	private final Actor viewing;
	private final Collection<WorldObject> newlyVisible;

	/**
	 * Construct a new WorldEvent with the given source and target.
	 *
	 * @param viewing The WorldObject to whom the newly visible objects have
	 * become visible
	 * @param newlyVisible The WorldObjects which have become visible.
	 */
	public NewlyVisibleEvent(Actor viewing, Collection<WorldObject> newlyVisible) {
		this.viewing = viewing;
		this.newlyVisible = newlyVisible;
	}

	/**
	 * Get the WorldObject to whom the newly visible objects have become
	 * visible.
	 *
	 * @return The WorldObject to whom the newly visible objects have become
	 * visible.
	 */
	public Actor getViewing() { return viewing; }

	/**
	 * Get the WorldObjects which have become visible.
	 *
	 * @return The WorldObjects which have become visible.
	 */
	public Iterable<WorldObject> getNewlyVisible() {
		return new LinkedList<>(newlyVisible);
	}
}
