package com.jenjinstudios.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.Client;

import java.util.logging.Level;

/**
 * Handles an invalid message received from the server.
 * @author Caleb Brinkman
 */
public class ClientExecutableInvalidMessage extends ClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	protected ClientExecutableInvalidMessage(Client client, Message message) {
		super(client, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runSynced() {
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runASync() {
		String messageName = (String) getMessage().getArgument("messageName");
		short messageID = (short) getMessage().getArgument("messageID");
		String reportMessage = "Server reported invalid sent message: " + messageName + " (ID:  " + messageID + ")";
		Client.LOGGER.log(Level.SEVERE, reportMessage);
	}
}
