package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.ExecutableMessage;
import com.jenjinstudios.core.io.Message;

/**
 * Used to determine the time taken to send a "ping" to a connection.
 *
 * @author Caleb Brinkman
 */
public class ExecutablePingResponse extends ExecutableMessage
{
    private final Connection connection;

    /**
     * Construct a new {@code PingResponse}.
     *
     * @param connection The connection invoking this executable message.
     * @param message The message which caused this executable message to be invoked.
     */
    public ExecutablePingResponse(Connection connection, Message message) {
        super(connection, message);

        this.connection = connection;
    }

    @Override
    public void runImmediate() {
		long requestTime = (long) getMessage().getArgument("requestTimeMillis");
		connection.getPingTracker().addPingTime(System.currentTimeMillis() - requestTime);
	}
}
