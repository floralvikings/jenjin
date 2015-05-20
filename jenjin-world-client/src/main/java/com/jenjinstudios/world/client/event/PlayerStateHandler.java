package com.jenjinstudios.world.client.event;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.event.WorldEventHandler;

import static com.jenjinstudios.world.client.message
	  .WorldClientMessageFactory.generateStateChangeRequest;

/**
 * Handles a StateChangeRequestEvent.
 *
 * @author Caleb Brinkman
 */
public class PlayerStateHandler
	  extends WorldEventHandler<PlayerStateEvent>
{
	@Override
	public void handle(PlayerStateEvent event) {
		Message request = generateStateChangeRequest(event.getState());
		event.getContext().enqueue(request);
	}
}
