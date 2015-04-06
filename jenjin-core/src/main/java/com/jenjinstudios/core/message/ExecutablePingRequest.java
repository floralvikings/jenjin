package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

/**
 * Used to determine the time taken to send a "ping" to a connection.
 *
 * @author Caleb Brinkman
 */
public class ExecutablePingRequest extends ExecutableMessage<MessageContext>
{

	/**
	 * Construct a new PingRequest.
     *
     * @param connection The connection invoking this executable message.
     * @param message The message which caused this executable message to be invoked.
	 * @param context The context in which to execute the message.
	 */
	public ExecutablePingRequest(Connection connection, Message message, MessageContext context) {
		super(
		  message, context); }

    /**
     * Generate a PingResponse with the given time of request.
     *
     * @param requestTimeMillis The time at which the request for this response was made.
     *
     * @return The generated PingResponse.
     */
    private static Message generatePingResponse(long requestTimeMillis) {
		Message pingResponse = MessageRegistry.getGlobalRegistry().createMessage("PingResponse");
		pingResponse.setArgument("requestTimeMillis", requestTimeMillis);
        return pingResponse;
    }

    @Override
	public Message execute() {
		long requestTimeNanos = (long) getMessage().getArgument("requestTimeMillis");
		return generatePingResponse(requestTimeNanos);
	}

}
