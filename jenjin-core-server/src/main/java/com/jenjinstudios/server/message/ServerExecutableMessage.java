package com.jenjinstudios.server.message;

import com.jenjinstudios.core.ExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ClientHandler;

/**
 * The ServerExecutableMessage class is invoked to respond to messages server-side.
 *
 * @author Caleb Brinkman
 */
public abstract class ServerExecutableMessage extends ExecutableMessage
{
    /** The ClientHandler for this object. */
    private final ClientHandler clientHandler;

    /**
     * Construct a new ExecutableMessage.  Must be implemented by subclasses.
     *
     * @param handler The handler using this ExecutableMessage.
     * @param message The message.
     */
    protected ServerExecutableMessage(ClientHandler handler, Message message) {
        super(handler, message);
        clientHandler = handler;
    }

    /**
     * Get the ClientHandler invoking this message.
     *
     * @return The ClientHandler invoking this message.
     */
    protected ClientHandler getClientHandler() {
        return clientHandler;
    }
}
