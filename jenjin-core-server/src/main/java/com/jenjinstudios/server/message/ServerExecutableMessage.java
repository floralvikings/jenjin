package com.jenjinstudios.server.message;

import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ClientHandler;

/**
 * The ServerExecutableMessage class is invoked to respond to messages server-side.
 *
 * @author Caleb Brinkman
 */
public abstract class ServerExecutableMessage<T extends MessageContext> extends ExecutableMessage<T>
{

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
     *
     * @param handler The handler using this ExecutableMessage.
     * @param message The message.
	 * @param context The context in which to execute the message.
	 */
	protected ServerExecutableMessage(ClientHandler handler, Message message, T context) {
		super(handler, message, context);
	}

    /**
     * Get the ClientHandler invoking this message.
     *
     * @return The ClientHandler invoking this message.
     */
    protected ClientHandler getClientHandler() {
		return (ClientHandler) getThreadPool();
	}
}
