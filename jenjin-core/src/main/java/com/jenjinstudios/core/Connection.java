package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageTypeException;
import com.jenjinstudios.core.util.MessageFactory;
import com.jenjinstudios.core.util.SecurityUtil;

import java.security.KeyPair;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code Connection} class is a subclass of the {@code Thread} class; it will loop indefinitely until the {@code
 * shutdown} method is called, reading {@code Message} objects from a stream, and invoking the correct implementation of
 * {@code ExecutableMessage} each time a new message is received.
 *
 * @author Caleb Brinkman
 */
public class Connection extends Thread
{
	private static final int MAX_INVALID_MESSAGES = 10;
	private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
	private final PingTracker pingTracker;
	private final ExecutableMessageQueue executableMessageQueue;
	private final MessageFactory messageFactory;
	private final MessageIO messageIO;
	private final MessageExecutor messageExecutor;
	private int invalidMsgCount;

	/**
	 * Construct a new {@code Connection} that utilizes the specified {@code MessageIO} to read and write messages.
	 *
	 * @param streams The {@code MessageIO} containing streams used to read and write messages.
	 */
	protected Connection(MessageIO streams) {
		this.messageIO = streams;
		pingTracker = new PingTracker();
		executableMessageQueue = new ExecutableMessageQueue();
		messageFactory = new MessageFactory();
		messageExecutor = new MessageExecutor(this);
		KeyPair rsaKeyPair = SecurityUtil.generateRSAKeyPair();
		if (rsaKeyPair != null)
		{
			messageIO.getIn().setPrivateKey(rsaKeyPair.getPrivate());
			Message message = MessageFactory.generatePublicKeyMessage(rsaKeyPair.getPublic());
			getMessageIO().queueOutgoingMessage(message);
		}
	}

	/**
	 * Get the MessageIO containing the keys and streams used by this connection.
	 *
	 * @return The MessageIO containing the keys and streams used by this connection.
	 */
	public MessageIO getMessageIO() { return messageIO; }

	@Override
	// TODO Extract MessageReaderThread class; responsibility should really be split up.
	public void run()
	{
		while (invalidMsgCount < MAX_INVALID_MESSAGES && messageExecutor.processNextIncomingMessage())
		{
			Thread.yield();
		}
		shutdown();
	}

	/**
	 * Execute the {@code runDelayed} method of each {@code ExecutableMessage} in the queue, in the order in which
	 * their
	 * {@code Message} objects were received.
	 */
	public void runQueuedExecutableMessages() { executableMessageQueue.runQueuedExecutableMessages(); }

	/**
	 * Get the {@code MessageFactory} to be used to generate the {@code Message} objects to be written by this {@code
	 * Connection}.
	 *
	 * @return The {@code MessageFactory} to be used to generate the {@code Message} objects to be written by this
	 * {@code Connection}.
	 */
	// TODO This should really be a static class with Connections passed as parameters.
	public MessageFactory getMessageFactory() { return messageFactory; }

	/**
	 * Get the PingTracker used by this connection to track latency.
	 *
	 * @return The PingTracker used by this connection to track latency.
	 */
	public PingTracker getPingTracker() { return pingTracker; }

	/**
	 * Get the {@code ExecutableMessageQueue} maintained by this connection.
	 *
	 * @return The {@code ExecutableMessageQueue} maintained by this connection.
	 */
	public ExecutableMessageQueue getExecutableMessageQueue() { return executableMessageQueue; }

	/**
	 * End this connection's execution loop and close any streams.
	 */
	public void shutdown() {
		messageIO.closeInputStream();
		messageIO.closeOutputStream();
	}

	void reportInvalidMessage(MessageTypeException e) {
		LOGGER.log(Level.WARNING, "Input stream reported invalid message receipt.");
		Message unknown = MessageFactory.generateInvalidMessage(e.getId(), "Unknown");
		getMessageIO().queueOutgoingMessage(unknown);
		invalidMsgCount++;
	}
}
