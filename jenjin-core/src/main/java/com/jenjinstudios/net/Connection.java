package com.jenjinstudios.net;

import com.jenjinstudios.io.*;
import com.jenjinstudios.util.MessageFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The communicator class is the superclass for any classes that communicate over socket.
 * @author Caleb Brinkman
 */
public abstract class Connection extends Thread
{
	/** The logger used for this class. */
	protected static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
	/** The list of collected ping times. */
	private final ArrayList<Long> pingTimes;
	/** The collection of messages to send at the next broadcast. */
	private final LinkedList<Message> outgoingMessages;
	/** The "one-shot" tasks to be executed in the current client loop. */
	private final LinkedList<Runnable> syncedTasks;
	private MessageFactory messageFactory;
	/** Flags whether the client threads should be running. */
	private volatile boolean running;
	/** The input stream used to read messages. */
	private MessageInputStream inputStream;
	/** The output stream used to write messages. */
	private MessageOutputStream outputStream;
	/** The socket used to connect. */
	private Socket socket;
	/** Flags whether this client is connected. */
	private volatile boolean connected;
	/** The AES key of this client. */
	private byte[] aesKey;
	/** The message registry for this class. */
	private MessageRegistry messageRegistry;

	/** Construct a new Communicator. */
	protected Connection() {
		outgoingMessages = new LinkedList<>();
		pingTimes = new ArrayList<>();
		syncedTasks = new LinkedList<>();
		messageFactory = new MessageFactory(this);
	}

	/**
	 * Set the socket used by this communicator.  Should only be called from subclass.
	 * @param socket The socket to be used by this communicator.
	 * @throws IOException If there is an exception creating message streams.
	 */
	public void setSocket(Socket socket) throws IOException {
		this.socket = socket;
		setOutputStream(new MessageOutputStream(this, socket.getOutputStream()));
		setInputStream(new MessageInputStream(this, socket.getInputStream()));
	}

	/** Send a ping request. */
	public void sendPing() {
		Message pingRequest = messageFactory.generatePingRequest();
		queueMessage(pingRequest);
	}

	/**
	 * Queue a message in the outgoing messages. This method is thread safe.
	 * @param message The message to add to the outgoing queue.
	 */
	public void queueMessage(Message message) {
		synchronized (outgoingMessages)
		{
			outgoingMessages.add(message);
		}
	}

	/**
	 * Add a ping time to the list.
	 * @param pingTime The time of the ping, in nanoseconds.
	 */
	public void addPingTime(long pingTime) {
		pingTimes.add(pingTime);
	}

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
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Set whether this communicator is connected.  Should only be called from subclass.
	 * @param connected Whether this communicator is connected.
	 */
	protected void setConnected(boolean connected) {
		this.connected = connected;
	}

	/**
	 * Get the MessageRegistry for this Connection.
	 * @return The MessageRegistry for this Connection.
	 */
	public MessageRegistry getMessageRegistry() { return messageRegistry; }

	/**
	 * Set the MessageRegistry for this Connection.
	 * @param messageRegistry The MessageRegistry for this Connection.
	 */
	protected void setMessageRegistry(MessageRegistry messageRegistry) { this.messageRegistry = messageRegistry; }

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
	public void writeMessage(Message o) {
		try
		{
			LOGGER.log(Level.FINEST, "Connection {0} writing message {1}", new Object[]{getName(), o});
			getOutputStream().writeMessage(o);
		} catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to write message " + o + " to socket, shutting down.", e);
			shutdown();
		}
	}

	/**
	 * Get the output stream used by this communicator.
	 * @return The output stream used by this communicator.
	 */
	public MessageOutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Set the output stream.
	 * @param outputStream The output stream.
	 */
	private void setOutputStream(MessageOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/** Shutdown this communicator. */
	public void shutdown() {
		running = false;
	}

	/**
	 * Get the AES key used by this client.
	 * @return The byte form of the AES key used by this client.
	 */
	public byte[] getAesKey() {
		return aesKey;
	}

	/**
	 * Set the AES key used by this client.
	 * @param key The key used by this client.
	 */
	public void setAESKey(byte[] key) {
		aesKey = key;
		getInputStream().setAESKey(key);
		getOutputStream().setAesKey(key);
	}

	/**
	 * Get the input stream used.
	 * @return The input stream.
	 */
	public MessageInputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Set the input stream used.
	 * @param inputStream The input stream.
	 */
	private void setInputStream(MessageInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void run() {
		running = true;
		try
		{
			Message currentMessage;
			while ((currentMessage = getInputStream().readMessage()) != null)
			{
				LOGGER.log(Level.FINEST, "Connection {0} reading message {1}", new Object[]{getName(), currentMessage});
				processMessage(currentMessage);
			}
		} finally
		{
			shutdown();
		}
	}

	/**
	 * Flags whether the client threads should be running.
	 * @return true if this client thread is still running.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * The "one-shot" tasks to be executed in the current client loop.
	 * @return The list of Synced Tasks
	 */
	public LinkedList<Runnable> getSyncedTasks() {
		LinkedList<Runnable> temp = new LinkedList<>();
		synchronized (syncedTasks)
		{
			temp.addAll(syncedTasks);
			syncedTasks.removeAll(temp);
		}
		return temp;
	}

	/** Run the list of synchronized tasks. */
	void runSyncedTasks() {
		for (Runnable r : getSyncedTasks())
			r.run();
	}

	/** Close the link with the server. */
	protected void closeLink() {
		try
		{
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ignored)
		{
			// Link closing, possible _because_ of an IOException; will be shutting down.
		} finally
		{
			connected = false;
		}
	}

	/**
	 * Get an executable message for a given message.
	 * @param message The message to be used.
	 * @return The ExecutableMessage.
	 */
	protected abstract ExecutableMessage getExecutableMessage(Message message);

	/**
	 * Process the specified message.  This method should be overridden by any implementing classes, but it does contain
	 * functionality necessary to communicate with a DownloadServer or a ChatServer.
	 * @param message The message to be processed.
	 */
	protected void processMessage(Message message) {
		ExecutableMessage exec = getExecutableMessage(message);
		if (exec != null)
		{
			exec.runASync();
			synchronized (syncedTasks)
			{
				syncedTasks.add(exec);
			}
		} else
		{
			Message invalid = messageFactory.generateInvalidMessage(message);
			queueMessage(invalid);
		}
	}

	public MessageFactory getMessageFactory() {	return messageFactory; }
}
