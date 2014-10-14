package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageTypeException;
import com.jenjinstudios.core.message.ExecutableMessage;
import com.jenjinstudios.core.util.MessageFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to process incoming {@code Message} objects, and invoke the appropriate {@code ExecutableMessage}.
 *
 * @author Caleb Brinkman
 */
public class MessageExecutor
{
	private static final Logger LOGGER = Logger.getLogger(MessageExecutor.class.getName());
	private final Connection connection;
	private final MessageInputStream inputStream;

	/**
	 * Construct a new {@code MessageExecutor} working for the given {@code Connection} reading from the given {@code
	 * MessageInputStream}.
	 *  @param connection The connection.
	 *
	 */
	public MessageExecutor(Connection connection) {
		this.connection = connection;
		this.inputStream = connection.getMessageIO().getIn();
	}

	boolean processNextIncomingMessage() {
		boolean success = true;
		try
		{
			Message currentMessage = inputStream.readMessage();
			LOGGER.log(Level.FINEST, "Reading message: {0}", currentMessage);
			executeMessage(currentMessage);
		} catch (MessageTypeException e)
		{
			connection.reportInvalidMessage(e);
		} catch (IOException e)
		{
			LOGGER.log(Level.FINE, "IOException when attempting to read from stream.", e);
			success = false;
		}
		return success;
	}

	void executeMessage(Message message) {
		ExecutableMessage exec = ExecutableMessage.getExecutableMessageFor(connection, message);
		if (exec != null)
		{
			processExecutableMessage(exec);
		} else
		{
			processInvalidMessage(message);
		}
	}

	private void processInvalidMessage(Message message) {
		Message invalid = MessageFactory.generateInvalidMessage(message.getID(), message.name);
		connection.getMessageIO().queueOutgoingMessage(invalid);
	}

	private void processExecutableMessage(ExecutableMessage exec) {
		exec.runImmediate();
		connection.getExecutableMessageQueue().queueExecutableMessage(exec);
	}

}
