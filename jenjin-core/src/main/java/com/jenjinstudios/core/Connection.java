package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageTypeException;
import com.jenjinstudios.core.util.MessageFactory;
import com.jenjinstudios.core.util.SecurityUtil;

import java.io.IOException;
import java.security.KeyPair;
import java.util.LinkedList;
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
	private final LinkedList<Message> outgoingMessages;
	private final ExecutableMessageQueue executableMessageQueue;
	private final MessageFactory messageFactory;
	private final MessageIO messageIO;
	private final MessageExecutor messageExecutor;
	private int invalidMsgCount;
	private boolean running;

	/**
	 * Construct a new {@code Connection} that utilizes the specified {@code MessageIO} to read and write messages.
	 *
	 * @param streams The {@code MessageIO} containing streams used to read and write messages.
	 */
	protected Connection(MessageIO streams) {
		this.messageIO = streams;
		outgoingMessages = new LinkedList<>();
		pingTracker = new PingTracker();
		executableMessageQueue = new ExecutableMessageQueue();
		messageFactory = new MessageFactory();
		messageExecutor = new MessageExecutor(this, messageIO.getIn());
		KeyPair rsaKeyPair = SecurityUtil.generateRSAKeyPair();
		if (rsaKeyPair != null)
		{
			messageIO.getIn().setPrivateKey(rsaKeyPair.getPrivate());
			Message message = MessageFactory.generatePublicKeyMessage(rsaKeyPair.getPublic());
			queueOutgoingMessage(message);
		}
	}

	/**
	 * Get the MessageIO containing the keys and streams used by this connection.
	 *
	 * @return The MessageIO containing the keys and streams used by this connection.
	 */
	public MessageIO getMessageIO() { return messageIO; }

	/**
	 * Add the specified {@code Message} to the queue of outgoing messages.  This queue is written when {@code
	 * writeAllMessages} is called.
	 *
	 * @param message The {@code Message} to write.
	 */
	public void queueOutgoingMessage(Message message) {
		if (messageIO.getOut().isClosed())
		{
			throw new MessageQueueException(message);
		}
		synchronized (outgoingMessages)
		{
			outgoingMessages.add(message);
		}
	}

	/**
	 * Write all the messages in the outgoing messages queue to the output stream.
	 */
	public void writeAllMessages() {
		synchronized (outgoingMessages)
		{
			while (!outgoingMessages.isEmpty())
			{
				writeMessage(outgoingMessages.remove());
			}
		}
	}

	@Override
	// TODO Extract MessageReaderThread class; responsibility should really be split up.
	public void run()
	{
		running = true;
		while (running && invalidMsgCount < MAX_INVALID_MESSAGES && messageExecutor.processNextIncomingMessage())
		{
			Thread.yield();
		}
		if (running)
		{
			shutdown();
		}
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
	protected void shutdown() {
		running = false;
		messageIO.closeInputStream();
		messageIO.closeOutputStream();
	}

	// TODO Move this method into the MessageIO class
	void writeMessage(Message o) {
		try
		{
			LOGGER.log(Level.FINEST, "Connection {0} writing message {1}", new Object[]{getName(), o});
			messageIO.getOut().writeMessage(o);
		} catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to write message " + o + " to socket, shutting down.", e);
			shutdown();
		}
	}

	void reportInvalidMessage(MessageTypeException e) {
		LOGGER.log(Level.WARNING, "Input stream reported invalid message receipt.");
		Message unknown = MessageFactory.generateInvalidMessage(e.getId(), "Unknown");
		queueOutgoingMessage(unknown);
		invalidMsgCount++;
	}
}
