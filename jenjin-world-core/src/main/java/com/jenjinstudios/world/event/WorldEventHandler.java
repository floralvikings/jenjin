package com.jenjinstudios.world.event;

/**
 * Superclass for classes which handle WorldEvents.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface WorldEventHandler<E extends WorldEvent>
{
	/**
	 * Handle the specified event.
	 *
	 * @param event The event.
	 */
	void handle(E event);
}
