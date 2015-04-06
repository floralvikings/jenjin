package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClientMessageContext;

/**
 * This class is the superclass for all ExecutableMessages that are invoked by the WorldClient.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldClientExecutableMessage<T extends WorldClientMessageContext> extends ExecutableMessage<T>
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
	 * @param client The client invoking this message.
	 * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	protected WorldClientExecutableMessage(Message message, T context) {
		super(message, context);
	}

}
