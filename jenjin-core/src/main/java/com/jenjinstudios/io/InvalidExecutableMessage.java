package com.jenjinstudios.io;

import com.jenjinstudios.net.Connection;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public class InvalidExecutableMessage extends ExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(InvalidExecutableMessage.class.getName());

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param message The Message.
	 */
	protected InvalidExecutableMessage(Connection connection, Message message) {
		super(message);
		LOGGER.log(Level.INFO, "InvalidExecutableMessage created for connection {0}", connection);
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
		String reportMessage = "Connection reported invalid sent message: " + messageName + " (ID:  " + messageID + ")";
		LOGGER.log(Level.SEVERE, reportMessage);
	}
}
