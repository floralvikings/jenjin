package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.Client;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.ExecutableMessage;

/**
 * Superclass of ExecutableMessages used by the client.
 *
 * @author Caleb Brinkman
 */
// TODO This class can probably be refactored away; should the subclasses just cast the constructor arguments?
public abstract class ClientExecutableMessage extends ExecutableMessage
{
    /** The client invoking this ExecutableMessage. */
    private final Client client;

    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
     */
    ClientExecutableMessage(Client client, Message message) {
        super(client, message);
        this.client = client;
    }

    /**
     * Get the client invoking this ExecutableMessage.
     *
     * @return The client invoking this ExecutableMessage.
     */
    public Client getConnection() {
        return client;
    }
}
