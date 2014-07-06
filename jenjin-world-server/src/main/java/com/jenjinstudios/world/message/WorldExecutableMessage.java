package com.jenjinstudios.world.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.message.ServerExecutableMessage;
import com.jenjinstudios.world.WorldClientHandler;

/**
 * Handles messages from clients of the game world.
 * @author Caleb Brinkman
 */
public abstract class WorldExecutableMessage extends ServerExecutableMessage
{
	/** The WorldClientHandler for which this executable message works. */
	private final WorldClientHandler handler;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected WorldExecutableMessage(WorldClientHandler handler, Message message) {
		super(handler, message);
		this.handler = handler;
	}

	@Override
	protected WorldClientHandler getClientHandler() {
		return handler;
	}
}
