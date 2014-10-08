package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

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
