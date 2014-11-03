package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.util.MessageFactory;
import com.jenjinstudios.core.util.SecurityUtil;

import java.security.KeyPair;

/**
 * The {@code Connection} class is a subclass of the {@code Thread} class; it will loop indefinitely until the {@code
 * shutdown} method is called, reading {@code Message} objects from a stream, and invoking the correct implementation of
 * {@code ExecutableMessage} each time a new message is received.
 *
 * @author Caleb Brinkman
 */
public class Connection
{
	private final PingTracker pingTracker;
	private final ExecutableMessageQueue executableMessageQueue;
	private final MessageFactory messageFactory;
	private final MessageIO messageIO;
	private final Thread messageReaderThread;
	private String name = "Connection";

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
		messageReaderThread = new Thread(new RunnableMessageReader(this));
		KeyPair rsaKeyPair = SecurityUtil.generateRSAKeyPair();
		if (rsaKeyPair != null)
		{
			messageIO.getIn().setPrivateKey(rsaKeyPair.getPrivate());
			Message message = MessageFactory.generatePublicKeyMessage(rsaKeyPair.getPublic());
			getMessageIO().queueOutgoingMessage(message);
		}
	}

	/**
	 * Start the message reader thread managed by this connection.
	 */
	public void start() {
		messageReaderThread.start();
	}

	/**
	 * Get the MessageIO containing the keys and streams used by this connection.
	 *
	 * @return The MessageIO containing the keys and streams used by this connection.
	 */
	public MessageIO getMessageIO() { return messageIO; }

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

	/**
	 * Get the name of this {@code Connection}.
	 *
	 * @return The name of this {@code Connection}.
	 */
	public String getName() { return name; }

	/**
	 * Set the name of this {@code Connection}.
	 *
	 * @param name The name of this {@code Connection}.
	 */
	public void setName(String name) { this.name = name; }
}
