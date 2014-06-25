package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

/**
 * Used to process a ping response message.
 * @author Caleb Brinkman
 */
public class ExecutablePingResponse extends ExecutableMessage
{
	/** The connection. */
	private final Connection connection;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param connection The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutablePingResponse(Connection connection, Message message) {
		super(message);

		this.connection = connection;
	}

	/** Run the synced portion of this message. */
	@Override
	public void runDelayed() {
		long requestTime = (long) getMessage().getArgument("requestTimeNanos");
		connection.getPingTracker().addPingTime((System.nanoTime() - requestTime) / 1000000);
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runImmediate() {

	}
}
