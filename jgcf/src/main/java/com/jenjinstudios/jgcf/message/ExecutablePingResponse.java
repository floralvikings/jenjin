package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.Client;
import com.jenjinstudios.message.Message;

/**
 * Used to process a ping response message.
 * @author Caleb Brinkman
 */
public class ExecutablePingResponse extends ClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutablePingResponse(Client client, Message message) {
		super(client, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runSynced() {
		long requestTime = (long) getMessage().getArgument("requestTimeNanos");
		long updateTime = getClient().getPeriod() * 1000000;
		getClient().addPingTime(System.nanoTime() - requestTime - updateTime);
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runASync() {

	}
}
