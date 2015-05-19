package com.jenjinstudios.world.event;

import com.jenjinstudios.world.object.WorldObject;

/**
 * Represents an even that will be dispatched to WorldObjectEventHandlers that
 * are registered to handle it.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldEvent
{
	private final WorldObject source;
	private final WorldObject target;

	/**
	 * Construct a new WorldEvent with the given source and target.
	 *
	 * @param source The source of the event.
	 * @param target The target of the event.
	 */
	public WorldEvent(WorldObject source, WorldObject target) {
		this.source = source;
		this.target = target;
	}

	/**
	 * Get the source of this event.
	 *
	 * @return The source of this event.
	 */
	public WorldObject getSource() { return source; }

	/**
	 * Get the target of this event.
	 *
	 * @return The target of this event.
	 */
	public WorldObject getTarget() { return target; }
}
