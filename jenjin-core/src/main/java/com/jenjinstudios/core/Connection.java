package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageTypeException;
import com.jenjinstudios.core.util.MessageFactory;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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
		KeyPair rsaKeyPair = generateRSAKeyPair();
		if (rsaKeyPair != null)
		{
			messageIO.getIn().setPrivateKey(rsaKeyPair.getPrivate());
			Message message = messageFactory.generatePublicKeyMessage(rsaKeyPair.getPublic());
			queueOutgoingMessage(message);
		}
	}

	/**
	 * Set the public key used to encrypt relevant messages.
	 *
	 * @param publicKey The public key.
	 */
	// TODO Move this method into the MessageIO class
	public void setPublicKey(PublicKey publicKey) { messageIO.getOut().setPublicKey(publicKey); }

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
	public void run() {
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
	 * Execute the {@code runDelayed} method of each {@code ExecutableMessage} in the queue,
	 * in the order in which their
	 * {@code Message} objects were received.
	 */
	public void runQueuedExecutableMessages() { executableMessageQueue.runQueuedExecutableMessages(); }

	public MessageFactory getMessageFactory() { return messageFactory; }

	public PingTracker getPingTracker() { return pingTracker; }

	public ExecutableMessageQueue getExecutableMessageQueue() { return executableMessageQueue; }

	// TODO Write test for this method.
	protected void shutdown() {
		running = false;
		closeLink();
	}

	// TODO Inline this method.
	protected void closeLink() {
		closeInputStream();
		closeOutputStream();
	}

	// TODO Extract this method
	private KeyPair generateRSAKeyPair() {
		KeyPair keyPair = null;
		try
		{
			KeyPairGenerator keyPairGenerator;
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create RSA key pair!", e);
		}
		return keyPair;
	}

	// TODO Move this method into MessageIO
	private void closeOutputStream() {
		try
		{
			messageIO.getOut().close();
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, "Issue closing output stream.", e);
		}
	}

	// TODO Move this method into MessageIO
	private void closeInputStream() {
		try
		{
			messageIO.getIn().close();
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, "Issue closing input stream.", e);
		}
	}

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
		Message unknown = messageFactory.generateInvalidMessage(e.getId(), "Unknown");
		queueOutgoingMessage(unknown);
		invalidMsgCount++;
	}
}
