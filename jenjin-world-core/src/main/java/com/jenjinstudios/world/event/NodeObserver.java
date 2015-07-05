package com.jenjinstudios.world.event;

import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.reflection.DynamicInvocationException;
import com.jenjinstudios.world.reflection.DynamicMethodSelector;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Observes WorldObjects, and dispatches events when necessary.
 *
 * @author Caleb Brinkman
 */
public abstract class NodeObserver<E extends NodeEvent>
{
	private static final Logger LOGGER = Logger.getLogger(NodeObserver.class.getName());
	private final Collection<NodeEventHandler<E>> handlers = new LinkedList<>();

	/**
	 * Register the given event handler, to which events will be dispatched as
	 * they are observed.
	 *
	 * @param handler The event handler.
	 */
	public void registerEventHandler(NodeEventHandler<E> handler) {
		handlers.add(handler);
	}

	/**
	 * Unregister the given event handler; after this method is called, no more
	 * events will be dispatched from this observer to that handler.
	 *
	 * @param handler The handler to be unregistered.
	 */
	public void unregisterEventHandler(NodeEventHandler<E> handler) {
		handlers.remove(handler);
	}

	/**
	 * Get an unmodifiable copy of this Observer's collection of event handlers.
	 *
	 * @return An <b>unmodifiable</b> copy of this Observer's event handlers.
	 */
	public Collection<NodeEventHandler<E>> getEventHandlers() { return Collections.unmodifiableCollection(handlers); }

	/**
	 * Observe the given node for an event occurring
	 * pre-update, and dispatch the event if necessary.
	 *
	 * @param node The node to watch for an event.
	 */
	public void onPreUpdate(Node node) {
		DynamicMethodSelector methodSelector = new DynamicMethodSelector(this);
		try {
			E event = (E) methodSelector.invokeMostSpecificMethod("observePreUpdate", node);
			if (event != null) {
				handlers.forEach(handler -> handler.handle(event));
			}
		} catch (DynamicInvocationException e) {
			LOGGER.log(Level.WARNING, "Exception when observing node", e);
		} catch (ClassCastException e) {
			LOGGER.log(Level.WARNING, "Method return type incorrect", e);
		}
	}

	/**
	 * Observe the given node for an event occurring
	 * in-update, and dispatch the event if necessary.
	 *
	 * @param node The node to watch for an event.
	 */
	public void onUpdate(Node node) {
		DynamicMethodSelector methodSelector = new DynamicMethodSelector(this);
		try {
			E event = (E) methodSelector.invokeMostSpecificMethod("observeUpdate", node);
			if (event != null) {
				handlers.forEach(handler -> handler.handle(event));
			}
		} catch (DynamicInvocationException e) {
			LOGGER.log(Level.WARNING, "Exception when observing node", e);
		} catch (ClassCastException e) {
			LOGGER.log(Level.WARNING, "Method return type incorrect", e);
		}
	}

	/**
	 * Observe the given node for an event occurring
	 * post-update, and dispatch the event if necessary.
	 *
	 * @param node The node to watch for an event.
	 */
	public void onPostUpdate(Node node) {
		DynamicMethodSelector methodSelector = new DynamicMethodSelector(this);
		try {
			E event = (E) methodSelector.invokeMostSpecificMethod("observePostUpdate", node);
			if (event != null) {
				handlers.forEach(handler -> handler.handle(event));
			}
		} catch (DynamicInvocationException e) {
			LOGGER.log(Level.WARNING, "Exception when observing node", e);
		} catch (ClassCastException e) {
			LOGGER.log(Level.WARNING, "Method return type incorrect", e);
		}
	}
}
