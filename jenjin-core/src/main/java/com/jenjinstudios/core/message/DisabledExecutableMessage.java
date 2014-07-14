package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessage extends ExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(DisabledExecutableMessage.class.getName());

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param message The Message.
	 */
	public DisabledExecutableMessage(Connection connection, Message message) {
		super(message);
		LOGGER.log(Level.SEVERE, "DisabledExecutableMessage constructed in {0}", connection);
	}

	@Override
	public void runDelayed() { throw new IllegalStateException("This message should be disabled."); }

	@Override
	public void runImmediate() { throw new IllegalStateException("This message should be disabled."); }
}
