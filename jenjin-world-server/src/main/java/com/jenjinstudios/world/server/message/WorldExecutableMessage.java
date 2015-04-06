package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.message.ServerExecutableMessage;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServerMessageContext;

/**
 * Handles messages from clients of the game world.
 * @author Caleb Brinkman
 */
public abstract class WorldExecutableMessage<T extends WorldServerMessageContext> extends ServerExecutableMessage<T>
{

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 * @param context The context in which to execute the message.
	 */
	protected WorldExecutableMessage(WorldClientHandler handler, Message message, T context) {
		super(handler, message, context);
	}

}
