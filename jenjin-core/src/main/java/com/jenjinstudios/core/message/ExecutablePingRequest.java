package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

/**
 * Used to determine the time taken to send a "ping" to a connection.
 *
 * @author Caleb Brinkman
 */
public class ExecutablePingRequest extends ExecutableMessage
{
    private final Connection connection;

    /**
     * Construct a new PingRequest.
     *
     * @param connection The connection invoking this executable message.
     * @param message The message which caused this executable message to be invoked.
     */
    public ExecutablePingRequest(Connection connection, Message message) {
        super(message);
        this.connection = connection;
    }

    @Override
    public void runDelayed() {
    }

    @Override
    public void runImmediate() {
        long requestTimeNanos = (long) getMessage().getArgument("requestTimeMillis");

        Message pingResponse = MessageFactory
              .generatePingResponse(requestTimeNanos);
        connection.getMessageIO().queueOutgoingMessage(pingResponse);

    }

}
