package com.jenjinstudios.world.event;

import com.jenjinstudios.world.WorldObject;

/**
 * Represents an event which has occurred in the world.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldEvent
{
	private final WorldObject target;
	private final WorldObject source;

	/**
	 * Construct a new WorldEvent with the given source.
	 *
	 * @param target The target of this event; e.g. the object that should be affected by this event.
	 * @param source The WorldObject which caused this event.
	 */
	public WorldEvent(WorldObject target, WorldObject source) {
		this.target = target;
		this.source = source;
	}

	/**
	 * Get the source of this event.
	 *
	 * @return The WorldObject which caused this event.
	 */
	public WorldObject getSource() {
		return source;
	}

	/**
	 * Get the target of this event.
	 *
	 * @return The WorldObject which this event should affect.
	 */
	public WorldObject getTarget() { return target; }
}
