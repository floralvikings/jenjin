package com.jenjinstudios.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.client.net.ClientHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used only for testing disabled messages.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class DisabledExecutableMessage extends ServerExecutableMessage
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(DisabledExecutableMessage.class.getName());

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected DisabledExecutableMessage(ClientHandler handler, Message message) {
		super(handler, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runSynced() {
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runASync() {
		LOGGER.log(Level.SEVERE, "You should never see this, this message is disabled.");
	}
}
