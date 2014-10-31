package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageTypeException;
import com.jenjinstudios.core.message.ExecutableMessage;
import com.jenjinstudios.core.util.MessageFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to continuously read {@code Message} objects from a {@code MessageInputStream}, invoke the
 * appropriate {@code ExecutableMessage}, and store it so that the {@code runeDelayed} method may be called later.
 *
 * @author Caleb Brinkman
 */
public class RunnableMessageReader implements Runnable
{
	private static final int MAX_INVALID_MESSAGES = 10;
	private static final Logger LOGGER = Logger.getLogger(RunnableMessageReader.class.getName());
	private final Connection connection;
	private int invalidMsgCount;

	/**
	 * Construct a new {@code RunnableMessageReader} working for the given Connection.
	 *
	 * @param connection The {@code Connection} managing this reader.
	 */
	public RunnableMessageReader(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		while (invalidMsgCount < MAX_INVALID_MESSAGES && processNextIncomingMessage())
		{
			Thread.yield();
		}
	}

	boolean processNextIncomingMessage() {
		boolean success = true;
		try
		{
			Message currentMessage = connection.getMessageIO().getIn().readMessage();
			LOGGER.log(Level.FINEST, "Reading message: {0}", currentMessage);
			executeMessage(currentMessage);
		} catch (MessageTypeException e)
		{
			reportInvalidMessage(e);
		} catch (EOFException | SocketException e)
		{
			LOGGER.log(Level.FINER, "Connection closed: " + connection);
			success = false;
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

	void reportInvalidMessage(MessageTypeException e) {
		LOGGER.log(Level.WARNING, "Input stream reported invalid message receipt.");
		Message unknown = MessageFactory.generateInvalidMessage(e.getId(), "Unknown");
		connection.getMessageIO().queueOutgoingMessage(unknown);
		invalidMsgCount++;
	}
}
