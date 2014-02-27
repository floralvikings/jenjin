package com.jenjinstudios.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.ClientHandler;
import com.jenjinstudios.net.Server;

import java.util.logging.Level;

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
		String messageName = (String) getMessage().getArgument("messageName");
		short messageID = (short) getMessage().getArgument("messageID");
		String reportMessage = "Client reported invalid sent message: " + messageName + " (ID:  " + messageID + ")";
		Server.LOGGER.log(Level.SEVERE, reportMessage);
	}
}
