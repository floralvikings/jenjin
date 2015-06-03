package com.jenjinstudios.world.server.event;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.world.event.WorldEvent;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.state.MoveState;

/**
 * Dispatches when one object visible to another changes its movement state.
 *
 * @author Caleb Brinkman
 */
public class VisibleStateChangeEvent implements WorldEvent
{
	private final MoveState state;
	private final MessageContext context;
	private final WorldObject changed;

	/**
	 * Construct a new WorldEvent with the given source and target.
	 *
	 * @param changed The object which has changed state.
	 * @param state The target of the event.
	 * @param context The context through which the state change message should
	 * be broadcast
	 */
	public VisibleStateChangeEvent(WorldObject changed, MoveState state, MessageContext context)
	{
		this.changed = changed;
		this.state = state;
		this.context = context;
	}

	/**
	 * The change in state which triggered this event.
	 *
	 * @return The change in state which triggered this event.
	 */
	public MoveState getState() { return state; }

	/**
	 * Get the context that should be used to send state change notifications.
	 *
	 * @return The context that should be used to send state change
	 * notifications.
	 */
	public MessageContext getContext() { return context; }

	/**
	 * Get the world object whose state has changed.
	 *
	 * @return The world object whose state has changed.
	 */
	public WorldObject getChanged() { return changed; }
}
