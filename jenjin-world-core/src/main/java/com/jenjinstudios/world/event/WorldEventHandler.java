package com.jenjinstudios.world.event;

/**
 * Superclass for handlers of WorldEvents. The {@code handle} event should be
 * overwritten in subclasses to handle the event.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface WorldEventHandler<E extends WorldEvent>
{
	/**
	 * Handle the given event.
	 *
	 * @param event The event to be handled.
	 */
	void handle(E event);
}
