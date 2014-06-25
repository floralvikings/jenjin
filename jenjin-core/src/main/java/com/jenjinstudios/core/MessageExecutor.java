package com.jenjinstudios.core;

import com.jenjinstudios.core.io.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public class MessageExecutor
{
	private static final Logger LOGGER = Logger.getLogger(MessageExecutor.class.getName());
	private Connection connection;
	private final MessageInputStream inputStream;


	public MessageExecutor(Connection connection, MessageInputStream inputStream) {
		this.connection = connection;
		this.inputStream = inputStream;
	}

	boolean processNextIncomingMessage() {
		boolean success = true;
		try
		{
			Message currentMessage = inputStream.readMessage();
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
		Message invalid = connection.getMessageFactory().generateInvalidMessage(message.getID(), message.name);
		connection.queueOutgoingMessage(invalid);
	}

	private void processExecutableMessage(ExecutableMessage exec) {
		exec.runImmediate();
		connection.getExecutableMessageQueue().queueExecutableMessage(exec);
	}

}
