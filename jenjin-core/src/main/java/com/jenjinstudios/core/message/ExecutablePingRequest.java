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

		Message pingResponse = connection.getMessageFactory()
			  .generatePingResponse(requestTimeNanos);
		connection.queueOutgoingMessage(pingResponse);

	}

}
