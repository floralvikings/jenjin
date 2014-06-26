package com.jenjinstudios.core;

import com.jenjinstudios.core.io.*;
import com.jenjinstudios.core.util.MessageFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The communicator class is the superclass for any classes that communicate over socket.
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
	private final MessageInputStream inputStream;
	private final MessageOutputStream outputStream;
	private final MessageRegistry messageRegistry;
	private final MessageExecutor messageExecutor;
	private int invalidMsgCount;
	private boolean aesKeySet;
	private boolean running;

	protected Connection(MessageIO streams) {
		this.messageRegistry = streams.getMr();
		this.inputStream = streams.getIn();
		this.outputStream = streams.getOut();
		outgoingMessages = new LinkedList<>();
		pingTracker = new PingTracker();
		executableMessageQueue = new ExecutableMessageQueue();
		messageFactory = new MessageFactory(messageRegistry);
		messageExecutor = new MessageExecutor(this, inputStream);
	}

	public void queueOutgoingMessage(Message message) {
		if (outputStream.isClosed())
		{
			throw new MessageQueueException(message);
		}
		synchronized (outgoingMessages)
		{
			outgoingMessages.add(message);
		}
	}

	public void writeAllMessages() {
		synchronized (outgoingMessages)
		{
			while (!outgoingMessages.isEmpty())
			{
				writeMessage(outgoingMessages.remove());
			}
		}
	}

	void writeMessage(Message o) {
		try
		{
			LOGGER.log(Level.FINEST, "Connection {0} writing message {1}", new Object[]{getName(), o});
			outputStream.writeMessage(o);
		} catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to write message " + o + " to socket, shutting down.", e);
			shutdown();
		}
	}

	public void setAESKey(byte[] key) {
		aesKeySet = true;
		inputStream.setAESKey(key);
		outputStream.setAesKey(key);
	}

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

	void reportInvalidMessage(MessageTypeException e) {
		LOGGER.log(Level.WARNING, "Input stream reported invalid message receipt.");
		Message unknown = messageFactory.generateInvalidMessage(e.getId(), "Unknown");
		queueOutgoingMessage(unknown);
		invalidMsgCount++;
	}

	protected void shutdown() {
		running = false;
		closeLink();
	}

	protected void closeLink() {
		closeInputStream();
		closeOutputStream();
	}

	private void closeOutputStream() {
		try
		{
			outputStream.close();
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, "Issue closing output stream.", e);
		}
	}

	private void closeInputStream() {
		try
		{
			inputStream.close();
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, "Issue closing input stream.", e);
		}
	}

	public void runQueuedExecutableMessages() { executableMessageQueue.runQueuedExecutableMessages(); }

	public MessageFactory getMessageFactory() { return messageFactory; }

	public PingTracker getPingTracker() { return pingTracker; }

	public ExecutableMessageQueue getExecutableMessageQueue() { return executableMessageQueue; }

	protected boolean isAesKeySet() { return aesKeySet;}

	public MessageRegistry getMessageRegistry() { return messageRegistry; }
}
