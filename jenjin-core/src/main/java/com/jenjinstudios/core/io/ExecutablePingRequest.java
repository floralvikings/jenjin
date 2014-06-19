package com.jenjinstudios.core.io;

import com.jenjinstudios.core.Connection;

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
	public void runSynced() {
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runASync() {
		long requestTimeNanos = (long) getMessage().getArgument("requestTimeNanos");

		Message pingResponse = connection.getMessageFactory()
				.generatePingResponse(requestTimeNanos);
		connection.queueMessage(pingResponse);

	}

}
