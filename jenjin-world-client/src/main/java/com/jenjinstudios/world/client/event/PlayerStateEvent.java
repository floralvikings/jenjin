package com.jenjinstudios.world.client.event;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.world.event.WorldEvent;
import com.jenjinstudios.world.state.MoveState;

/**
 * Causes a StateChangeRequest to be sent when the event is dispatched.
 *
 * @author Caleb Brinkman
 */
public class PlayerStateEvent implements WorldEvent
{
	private final MoveState state;
	private final MessageContext context;

	/**
	 * Construct a new WorldEvent with the given source and target.
	 *
	 * @param state The move state for which the request will be sent.
	 * @param context The message context used to send a state change request
	 */
	public PlayerStateEvent(MoveState state, MessageContext context)
	{
		this.state = state;
		this.context = context;
	}

	/**
	 * Get the context into which the state change request should be passed as
	 * an output message.
	 *
	 * @return The message context.
	 */
	public MessageContext getContext() { return context; }

	/**
	 * Get the move state for which the request will be sent.
	 *
	 * @return The move state for which the request will be sent.
	 */
	public MoveState getState() { return state; }
}
