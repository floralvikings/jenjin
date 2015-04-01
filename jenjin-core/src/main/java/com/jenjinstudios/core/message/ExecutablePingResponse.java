package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;

/**
 * Used to determine the time taken to send a "ping" to a connection.
 *
 * @author Caleb Brinkman
 */
public class ExecutablePingResponse extends ExecutableMessage<MessageContext>
{
    /**
     * Construct a new {@code PingResponse}.
     *
     * @param connection The connection invoking this executable message.
     * @param message The message which caused this executable message to be invoked.
	 * @param context The context in which to execute the message.
	 */
	public ExecutablePingResponse(Connection connection, Message message, MessageContext context) {
		super(connection, message, context);
	}

    @Override
	public Message execute() {
		long requestTime = (long) getMessage().getArgument("requestTimeMillis");
		((Connection) getThreadPool()).getPingTracker().addPingTime(System.currentTimeMillis() - requestTime);
		return null;
	}
}
