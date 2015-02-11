package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

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

    /**
     * Generate a PingResponse with the given time of request.
     *
     * @param requestTimeMillis The time at which the request for this response was made.
     *
     * @return The generated PingResponse.
     */
    private static Message generatePingResponse(long requestTimeMillis) {
        Message pingResponse = MessageRegistry.getInstance().createMessage("PingResponse");
        pingResponse.setArgument("requestTimeMillis", requestTimeMillis);
        return pingResponse;
    }

    @Override
    public void runDelayed() {
    }

    @Override
    public void runImmediate() {
        long requestTimeNanos = (long) getMessage().getArgument("requestTimeMillis");

        Message pingResponse =
              generatePingResponse(requestTimeNanos);
        connection.getMessageIO().queueOutgoingMessage(pingResponse);

    }

}
