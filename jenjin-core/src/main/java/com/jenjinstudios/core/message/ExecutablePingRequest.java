package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

/**
 * Used to request a ping message.
 * @author Caleb Brinkman
 */
public class ExecutablePingRequest extends ExecutableMessage
{
	/** The connection. */
	private final Connection connection;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param connection The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutablePingRequest(Connection connection, Message message) {
		super(message);
		this.connection = connection;
	}

	/** Run the synced portion of this message. */
	@Override
	public void runDelayed() {
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runImmediate() {
		long requestTimeNanos = (long) getMessage().getArgument("requestTimeMillis");

		Message pingResponse = connection.getMessageFactory()
				.generatePingResponse(requestTimeNanos);
		connection.queueOutgoingMessage(pingResponse);

	}

}
