package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class exists for testing purposes only, not meant to be used in production code.
 *
 * @author Caleb Brinkman
 */
public class ExecutableBlankMessage extends ExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableBlankMessage.class.getName());
	private final Connection connection;

	@SuppressWarnings("WeakerAccess")
	public ExecutableBlankMessage(Connection connection, Message message) {
		super(message);
		this.connection = connection;
	}

	@Override
	public void runDelayed() {

	}

	@Override
	public void runImmediate() {
		LOGGER.log(Level.FINEST, "{0} received blank message.", connection.getName());
	}
}
