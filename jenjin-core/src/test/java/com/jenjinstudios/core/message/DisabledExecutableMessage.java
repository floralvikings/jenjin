package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test class; not intended to be used from any production code.
 *
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessage extends ExecutableMessage<MessageContext>
{
    private static final Logger LOGGER = Logger.getLogger(DisabledExecutableMessage.class.getName());

    /**
     * Cosntruct a new {@code DisabledExecutableMessage}; not meant to be used in production code.
     *
     * @param connection The connection invoking this executable message.
     * @param message The message that caused this executable message to be invoked.
	 * @param context The context in which to execute the message.
	 */
	public DisabledExecutableMessage(Connection connection, Message message, MessageContext context) {
		super(connection, message, context);
		LOGGER.log(Level.SEVERE, "DisabledExecutableMessage constructed in {0}", connection);
    }

    @Override
	public Message execute() { throw new IllegalStateException("This message should be disabled."); }
}
