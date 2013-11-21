package com.jenjinstudios.message;

import com.jenjinstudios.net.ClientHandler;

/**
 * Handles an invalid message received from a client.
 * @author Caleb Brinkman
 */
public class ServerExecutableInvalidMessage extends ServerExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ServerExecutableInvalidMessage(ClientHandler handler, Message message) {
		super(handler, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runSynced() {
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runASync() {
	}
}
