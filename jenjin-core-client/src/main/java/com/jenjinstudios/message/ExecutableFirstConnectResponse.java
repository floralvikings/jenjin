package com.jenjinstudios.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.net.Client;

/**
 * @author Caleb Brinkman
 */
public class ExecutableFirstConnectResponse extends ClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableFirstConnectResponse(Client client, Message message) {
		super(client, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runSynced() {
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runASync() {
		getClient().doPostConnectInit(getMessage());
	}
}
