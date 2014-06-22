package com.jenjinstudios.core;

import com.jenjinstudios.core.io.*;
import com.jenjinstudios.core.util.MessageFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The communicator class is the superclass for any classes that communicate over socket.
 * @author Caleb Brinkman
 */
public class Connection extends Thread
{
	/** The maximum number of permitted invalid messages before this connection shuts down. */
	public static final int MAX_INVALID_MESSAGES = 10;
	/** The logger used for this class. */
	private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
	/** The list of collected ping times. */
	private final List<Long> pingTimes;
	/** The collection of messages to send at the next broadcast. */
	private final LinkedList<Message> outgoingMessages;
	/** The "one-shot" tasks to be executed in the current client loop. */
	private final List<Runnable> syncedTasks;
	/** The MessageFactory used by this connection. */
	private final MessageFactory messageFactory;
	/** Flags whether the client threads should be running. */
	private volatile boolean running;
	/** The input stream used to read messages. */
	private final MessageInputStream inputStream;
	/** The output stream used to write messages. */
	private final MessageOutputStream outputStream;
	/** Flags whether this client is connected. */
	private volatile boolean connected;
	/** The AES key of this client. */
	private byte[] aesKey;
	/** The message registry for this class. */
	private final MessageRegistry messageRegistry;
	private int invalidMsgCount;

	protected Connection(MessageInputStream in, MessageOutputStream out, MessageRegistry mr) {
		this.messageRegistry = mr;
		this.inputStream = in;
		this.outputStream = out;
		outgoingMessages = new LinkedList<>();
		pingTimes = new ArrayList<>();
		syncedTasks = new LinkedList<>();
		messageFactory = new MessageFactory(this.messageRegistry);
	}

	/** Send a ping request. */
	protected void sendPing() {
		Message pingRequest = messageFactory.generatePingRequest();
		queueMessage(pingRequest);
	}

	/**
	 * Queue a message in the outgoing messages. This method is thread safe.
	 * @param message The message to add to the outgoing queue.
	 */
	public void queueMessage(Message message) {
		if (outputStream.isClosed())
		{
			throw new MessageQueueException(message);
		}
		synchronized (outgoingMessages)
		{
			outgoingMessages.add(message);
		}
	}

	/**
	 * Add a ping time to the list.
	 * @param pingTime The time of the ping, in nanoseconds.
	 */
	public void addPingTime(long pingTime) { pingTimes.add(pingTime); }

	/**
	 * Get the average ping time, in nanoseconds.
	 * @return The average ping time between client and server, in nanoseconds.
	 */
	public long getAveragePingTime() {
		long total = 0;
		int num;
		synchronized (pingTimes)
		{
			num = pingTimes.size();
			for (long l : pingTimes) total += l;
		}
		return total / num;
	}

	/**
	 * Flags whether this communicator is connected.
	 * @return true if this communicator is currently connected to a server.
	 */
	protected boolean isConnected() { return connected; }

	/**
	 * Set whether this communicator is connected.  Should only be called from subclass.
	 * @param connected Whether this communicator is connected.
	 */
	protected void setConnected(boolean connected) { this.connected = connected; }

	/**
	 * Get the MessageRegistry for this Connection.
	 * @return The MessageRegistry for this Connection.
	 */
	public MessageRegistry getMessageRegistry() { return messageRegistry; }

	/** Send all messages in the outgoing queue.  This method should only be called from the client update thread. */
	public void sendAllMessages() {
		synchronized (outgoingMessages)
		{
			while (!outgoingMessages.isEmpty())
			{
				writeMessage(outgoingMessages.remove());
			}
		}
	}

	/**
	 * Send the specified message to the client.
	 * @param o The message to send to the client.
	 */
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

	/** Shutdown this communicator. */
	protected void shutdown() { running = false; }

	/**
	 * Get the AES key used by this client.
	 * @return The byte form of the AES key used by this client.
	 */
	protected byte[] getAesKey() { return aesKey; }

	/**
	 * Set the AES key used by this client.
	 * @param key The key used by this client.
	 */
	public void setAESKey(byte[] key) {
		aesKey = key;
		inputStream.setAESKey(key);
		outputStream.setAesKey(key);
	}

	public void run() {
		running = true;
		Message currentMessage;
		while (invalidMsgCount < MAX_INVALID_MESSAGES)
		{
			try
			{
				currentMessage = inputStream.readMessage();
				processMessage(currentMessage);
			} catch (MessageTypeException e)
			{
				LOGGER.log(Level.WARNING, "Input stream reported invalid message receipt.");
				Message unknown = messageFactory.generateInvalidMessage(e.getId(), "Unknown");
				queueMessage(unknown);
				invalidMsgCount++;
			} catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, "IOException when attempting to read from stream.", e);
				break;
			}
			Thread.yield();
		}

		shutdown();
	}

	/**
	 * Flags whether the client threads should be running.
	 * @return true if this client thread is still running.
	 */
	public boolean isRunning() { return running; }

	/**
	 * The "one-shot" tasks to be executed in the current client loop.
	 * @return The list of Synced Tasks
	 */
	protected Iterable<Runnable> getSyncedTasks() {
		LinkedList<Runnable> temp = new LinkedList<>();
		synchronized (syncedTasks)
		{
			temp.addAll(syncedTasks);
			syncedTasks.removeAll(temp);
		}
		return temp;
	}

	/** Run the list of synchronized tasks. */
	public void runSyncedTasks() {
		for (Runnable r : getSyncedTasks())
			r.run();
	}

	/** Close the link with the server. */
	protected void closeLink() {
		try
		{
			inputStream.close();
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, "Issue closing input stream.", e);
		}
		try
		{
			outputStream.close();
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, "Issue closing output stream.", e);
		}
		connected = false;
	}

	/**
	 * Process the specified message.  This method should be overridden by any implementing classes, but it does contain
	 * functionality necessary to communicate with a DownloadServer or a ChatServer.
	 * @param message The message to be processed.
	 */
	private void processMessage(Message message) {
		ExecutableMessage exec = ExecutableMessage.getExecutableMessageFor(this, message);
		if (exec != null)
		{
			exec.runASync();
			synchronized (syncedTasks)
			{
				syncedTasks.add(exec);
			}
		} else
		{
			Message invalid = messageFactory.generateInvalidMessage(message.getID(), message.name);
			queueMessage(invalid);
		}
	}

	/**
	 * Get the message factory working for this connection.  This should be overridden in any classes that use a custom
	 * message factory (and they all should).
	 * @return The MessageFactory working for this connection.
	 */
	public MessageFactory getMessageFactory() {
		return messageFactory;
	}
}
