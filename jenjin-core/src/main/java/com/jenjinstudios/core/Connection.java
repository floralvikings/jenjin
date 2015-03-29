package com.jenjinstudios.core;

import com.jenjinstudios.core.concurrency.MessageExecutor;
import com.jenjinstudios.core.concurrency.MessageReader;
import com.jenjinstudios.core.concurrency.MessageWriter;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Connection class utilizes a MessageInputStream and MessageOutputStream to read and write messages to another
 * connection.  This is done with four seperate Threads, started using the start() method; one for reading messages, one
 * for executing the retrieved messages' ExecutableMessage equivalent, one for writing messages, and one for monitoring
 * the others for errors.
 *
 * @author Caleb Brinkman
 */
public class Connection
{
	private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
	private final PingTracker pingTracker;
	private final MessageIO messageIO;
	private final Timer checkErrorTimer;
	private final TimerTask checkErrorTask;
	private final MessageWriter messageWriter;
	private final MessageReader messageReader;
	private final MessageExecutor messageExecutor;
	private String name = "Connection";

	/**
	 * Construct a new connection using the given MessageIO for reading and writing messages.
	 *
	 * @param streams The MessageIO containing the input and output streams
	 */
	public Connection(MessageIO streams) {
		this.messageIO = streams;
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/core/io/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core XML Registry", stream);
		messageWriter = new MessageWriter(messageIO.getOut());
		pingTracker = new PingTracker();
		checkErrorTimer = new Timer();
		checkErrorTask = new CheckErrorsTask();
		messageReader = new MessageReader(messageIO.getIn());
		messageExecutor = new MessageExecutor(this, messageReader);
	}

	/**
	 * Queue up the supplied message to be written.
	 *
	 * @param message The message to be sent.
	 */
	public void enqueueMessage(Message message) { messageWriter.enqueue(message); }

	/**
	 * Start the message reader thread managed by this connection.
	 */
	public void start() {
		checkErrorTimer.scheduleAtFixedRate(checkErrorTask, 0, 10);
		messageExecutor.start();
		messageReader.start();
		messageWriter.start();
	}

	/**
	 * Get the MessageIO containing the keys and streams used by this connection.
	 *
	 * @return The MessageIO containing the keys and streams used by this connection.
	 */
	public MessageIO getMessageIO() { return messageIO; }

	/**
	 * Get the PingTracker used by this connection to track latency.
	 *
	 * @return The PingTracker used by this connection to track latency.
	 */
	public PingTracker getPingTracker() { return pingTracker; }

	/**
	 * End this connection's execution loop and close any streams.
	 */
	public void shutdown() {
		LOGGER.log(Level.INFO, "Shutting down connection: " + name);
		messageWriter.stop();
		messageReader.stop();
		checkErrorTimer.cancel();
		messageExecutor.stop();
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

	private class CheckErrorsTask extends TimerTask
	{
		@Override
		public void run() {
			if (messageReader.isErrored() || messageWriter.isErrored())
			{
				LOGGER.log(Level.SEVERE, "Message reader or writer in error state; shutting down.");
				shutdown();
			}
		}
	}
}
