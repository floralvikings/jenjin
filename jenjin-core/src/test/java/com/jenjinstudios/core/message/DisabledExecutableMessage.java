package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test class; not intended to be used from any production code.
 *
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessage extends ExecutableMessage
{
    private static final Logger LOGGER = Logger.getLogger(DisabledExecutableMessage.class.getName());

    /**
     * Cosntruct a new {@code DisabledExecutableMessage}; not meant to be used in production code.
     *
     * @param connection The connection invoking this executable message.
     * @param message The message that caused this executable message to be invoked.
     */
    public DisabledExecutableMessage(Connection connection, Message message) {
        super(connection, message);
        LOGGER.log(Level.SEVERE, "DisabledExecutableMessage constructed in {0}", connection);
    }

    @Override
	public void execute() { throw new IllegalStateException("This message should be disabled."); }
}
