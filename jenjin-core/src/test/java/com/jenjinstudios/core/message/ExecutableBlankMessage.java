package com.jenjinstudios.core.message;

import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class exists for testing purposes only, not meant to be used in production code.
 *
 * @author Caleb Brinkman
 */
public class ExecutableBlankMessage extends ExecutableMessage<MessageContext>
{
    private static final Logger LOGGER = Logger.getLogger(ExecutableBlankMessage.class.getName());

    /**
     * Construct a new ExecutableBlankMessage; not meant to be used in production code.
	 *  @param message The message causing this to be invoked.
	 * @param context The context in which to execute the message.
	 */
    @SuppressWarnings("WeakerAccess")
	public ExecutableBlankMessage(Message message, MessageContext context) {
		super(message, context);
	}

    @Override
	public Message execute() {
		LOGGER.log(Level.FINEST, "{0} received blank message.", getContext().getName());
		return null;
	}
}
