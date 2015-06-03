package com.jenjinstudios.world.event;

import com.jenjinstudios.world.Node;

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
	 * Observe the given node for an event occurring
	 * pre-update, and dispatch the event if necessary.
	 *
	 * @param node The node to watch for an event.
	 */
	public void onPreUpdate(Node node) {
		E event = observePreUpdate(node);
		if (event != null) {
			handlers.forEach(handler -> handler.handle(event));
		}
	}

	/**
	 * Observe the given node for an event occurring
	 * in-update, and dispatch the event if necessary.
	 *
	 * @param node The node to watch for an event.
	 */
	public void onUpdate(Node node) {
		E event = observeUpdate(node);
		if (event != null) {
			handlers.forEach(handler -> handler.handle(event));
		}
	}

	/**
	 * Observe the given node for an event occurring
	 * post-update, and dispatch the event if necessary.
	 *
	 * @param node The node to watch for an event.
	 */
	public void onPostUpdate(Node node) {
		E event = observePostUpdate(node);
		if (event != null) {
			handlers.forEach(handler -> handler.handle(event));
		}
	}

	/**
	 * Observe the given node for an event occurring
	 * pre-update, returning an event if it occurs, and null otherwise.
	 *
	 * @param node the node to watch for the event
	 *
	 * @return The event that occurred, if any; null otherwise.
	 */
	protected abstract E observePreUpdate(Node node);

	/**
	 * Observe the given node for an event occurring
	 * in-update, returning an event if it occurs, and null otherwise.
	 *
	 * @param node the node to watch for the event
	 *
	 * @return The event that occurred, if any; null otherwise.
	 */
	protected abstract E observeUpdate(Node node);

	/**
	 * Observe the given node for an event occurring
	 * post-update, returning an event if it occurs, and null otherwise.
	 *
	 * @param node the node to watch for the event
	 *
	 * @return The event that occurred, if any; null otherwise.
	 */
	protected abstract E observePostUpdate(Node node);
}
