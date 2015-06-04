package com.jenjinstudios.world.event;

import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Contains the object which is seeing the newly invisible objects, and a
 * collection of those objects.
 *
 * @author Caleb Brinkman
 */
public class NewlyInvisibleEvent implements WorldEvent
{
	private final WorldObject viewing;
	private final Collection<WorldObject> newlyInvisible;

	/**
	 * Construct a new WorldEvent with the given source and target.
	 *
	 * @param viewing The WorldObject to whom the newly visible objects have
	 * become visible
	 * @param newlyInvisible The WorldObjects which have become visible.
	 */
	public NewlyInvisibleEvent(WorldObject viewing,
							   Collection<WorldObject> newlyInvisible)
	{
		this.viewing = viewing;
		this.newlyInvisible = newlyInvisible;
	}

	/**
	 * Get the WorldObject to whom the newly visible objects have become
	 * visible.
	 *
	 * @return The WorldObject to whom the newly visible objects have become
	 * visible.
	 */
	public WorldObject getViewing() { return viewing; }

	/**
	 * Get the WorldObjects which have become visible.
	 *
	 * @return The WorldObjects which have become visible.
	 */
	public Iterable<WorldObject> getNewlyInvisible() {
		return new LinkedList<>(newlyInvisible);
	}
}
