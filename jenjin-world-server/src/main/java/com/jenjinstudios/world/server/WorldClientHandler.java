package com.jenjinstudios.world.server;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.MessageStreamPair;

/**
 * Handles clients for a world server.
 *
 * @author Caleb Brinkman
 */
public class WorldClientHandler extends Connection<WorldServerMessageContext>
{

	/**
	 * Construct a new WorldClientHandler.
	 *
	 * @param messageStreamPair The streams.
	 * @param context The context.
	 */
	public WorldClientHandler(MessageStreamPair messageStreamPair, WorldServerMessageContext
		  context) {
		super(messageStreamPair, context);
	}

	@Override
	public void shutdown() {
		if (getMessageContext().getUser() != null)
		{
			getMessageContext().getWorld().getWorldObjects().remove(getMessageContext().getUser().getId());
		}
		super.shutdown();
	}

	public Player getUser() { return getMessageContext().getUser(); }

}
