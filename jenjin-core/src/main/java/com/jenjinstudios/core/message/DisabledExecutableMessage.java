package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test class; not intended to be used from any production code.
 *
 * @author Caleb Brinkman
 */
// TODO Can this class be moved to the test directory?
public class DisabledExecutableMessage extends ExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(DisabledExecutableMessage.class.getName());

	public DisabledExecutableMessage(Connection connection, Message message) {
		super(message);
		LOGGER.log(Level.SEVERE, "DisabledExecutableMessage constructed in {0}", connection);
	}

	@Override
	public void runDelayed() { throw new IllegalStateException("This message should be disabled."); }

	@Override
	public void runImmediate() { throw new IllegalStateException("This message should be disabled."); }
}
