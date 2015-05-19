package com.jenjinstudios.world.event;

/**
 * Superclass for handlers of WorldEvents. The {@code handle} event should be
 * overwritten in subclasses to handle the event.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldObjectEventHandler<E extends WorldEvent>
{
	/**
	 * Handle the given event.
	 *
	 * @param event The event to be handled.
	 */
	public abstract void handle(E event);
}
