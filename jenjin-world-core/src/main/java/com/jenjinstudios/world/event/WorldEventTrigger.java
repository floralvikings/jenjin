package com.jenjinstudios.world.event;

import com.jenjinstudios.world.World;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Used to trigger events on a world object during, pre-update, in-update, and post-update cycles of the world update
 * loop.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldEventTrigger<E extends WorldEvent>
{
	private final Collection<WorldEventHandler<E>> eventHandlers = new LinkedList<>();

	/**
	 * Register a WorldEventHandler to be passed events that are raied by this trigger.
	 *
	 * @param eventHandler The WorldEventHandler to be registered.
	 */
	public void registerEventHandler(WorldEventHandler<E> eventHandler) {
		synchronized (eventHandlers) {
			eventHandlers.add(eventHandler);
		}
	}

	/**
	 * Remove a WorldEventHandler from the collection of registered handlers.
	 *
	 * @param eventHandler The WorldEventHandler to be unregistered.
	 */
	public void unregisterEventHandler(WorldEventHandler<E> eventHandler) {
		synchronized (eventHandlers) {
			eventHandlers.remove(eventHandler);
		}
	}

	/**
	 * Create and pass events into registered WorldEventHandlers to be handled.
	 */
	public void trigger() {
		E worldEvent = getEvent();
		synchronized (eventHandlers) {
			for (WorldEventHandler<E> eventHandler : eventHandlers) {
				eventHandler.handle(worldEvent);
			}
		}
	}

	/**
	 * Examine the specified World during the pre-update cycle and determing if an event should be triggered.
	 *
	 * @param world The world being examined for an event.
	 *
	 * @return {@code true} if an event should be triggered, {@code false} otherwise.
	 */
	public abstract boolean onPreUpdate(World world);

	/**
	 * Examine the specified World during the update cycle and determing if an event should be triggered.
	 *
	 * @param world The world being examined for an event.
	 *
	 * @return {@code true} if an event should be triggered, {@code false} otherwise.
	 */
	public abstract boolean onUpdate(World world);

	/**
	 * Examine the specified World during the post-update cycle and determing if an event should be triggered.
	 *
	 * @param world The world being examined for an event.
	 *
	 * @return {@code true} if an event should be triggered, {@code false} otherwise.
	 */
	public abstract boolean onPostUpdate(World world);

	/**
	 * Construct and return a WorldEvent to be passed into event handlers.
	 *
	 * @return The WorldEvent to be handled by event handlers.
	 */
	protected abstract E getEvent();
}
