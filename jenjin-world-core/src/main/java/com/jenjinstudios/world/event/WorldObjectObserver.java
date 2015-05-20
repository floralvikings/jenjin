package com.jenjinstudios.world.event;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Observes WorldObjects, and dispatches events when necessary.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldObjectObserver<E extends WorldEvent>
{
	private final Collection<WorldEventHandler<E>> handlers
		  = new LinkedList<>();

	/**
	 * Register the given event handler, to which events will be dispatched as
	 * they are observed.
	 *
	 * @param handler The event handler.
	 */
	public void registerEventHandler(WorldEventHandler<E> handler) {
		handlers.add(handler);
	}

	/**
	 * Unregister the given event handler; after this method is called, no more
	 * events will be dispatched from this observer to that handler.
	 *
	 * @param handler The handler to be unregistered.
	 */
	public void unregisterEventHandler(WorldEventHandler<E> handler) {
		handlers.remove(handler);
	}

	/**
	 * Observe the given WorldObject in the given World for an event occurring
	 * pre-update, and dispatch the event if necessary.
	 *
	 * @param world The world in which to watch for the event.
	 * @param obj The object to watch for the event.
	 */
	public void onPreUpdate(World world, WorldObject obj) {
		E event = observePreUpdate(world, obj);
		if (event != null) {
			handlers.forEach(handler -> handler.handle(event));
		}
	}

	/**
	 * Observe the given WorldObject in the given World for an event occurring
	 * in-update, and dispatch the event if necessary.
	 *
	 * @param world The world in which to watch for the event.
	 * @param obj The object to watch for the event.
	 */
	public void onUpdate(World world, WorldObject obj) {
		E event = observeUpdate(world, obj);
		if (event != null) {
			handlers.forEach(handler -> handler.handle(event));
		}
	}

	/**
	 * Observe the given WorldObject in the given World for an event occurring
	 * post-update, and dispatch the event if necessary.
	 *
	 * @param world The world in which to watch for the event.
	 * @param obj The object to watch for the event.
	 */
	public void onPostUpdate(World world, WorldObject obj) {
		E event = observePostUpdate(world, obj);
		if (event != null) {
			handlers.forEach(handler -> handler.handle(event));
		}
	}

	/**
	 * Observe the given WorldObject in the given World for an event occurring
	 * pre-update, returning an event if it occurs, and null otherwise.
	 *
	 * @param world The world in which to watch for the event.
	 * @param obj The object to watch for the event.
	 *
	 * @return The event that occurred, if any; null otherwise.
	 */
	public abstract E observePreUpdate(World world, WorldObject obj);

	/**
	 * Observe the given WorldObject in the given World for an event ocurring
	 * in-update, returning an event if it occurs, and null otherwise.
	 *
	 * @param world The world in which to watch for the event.
	 * @param obj The object to watch for the event.
	 *
	 * @return The event that occurred, if any; null otherwise.
	 */
	public abstract E observeUpdate(World world, WorldObject obj);

	/**
	 * Observe the given WorldObject in the given World for an event ocurring
	 * post-update, returning an event if it occurs, and null otherwise.
	 *
	 * @param world The world in which to watch for the event.
	 * @param obj The object to watch for the event.
	 *
	 * @return The event that occurred, if any; null otherwise.
	 */
	public abstract E observePostUpdate(World world, WorldObject obj);
}
