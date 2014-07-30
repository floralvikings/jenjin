package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public class ExecutableBlankMessage extends ExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableBlankMessage.class.getName());
	private final Connection connection;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param message The Message.
	 */
	@SuppressWarnings("WeakerAccess")
	public ExecutableBlankMessage(Connection connection, Message message) {
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
		LOGGER.log(Level.FINEST, "{0} received blank message.", connection.getName());
	}
}
