package com.jenjinstudios.core.concurrency;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageStreamPair;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages all four threads necessary for proper, asynchronous message IO.
 *
 * @author Caleb Brinkman
 */
public class MessageThreadPool
{
	private static final Logger LOGGER = Logger.getLogger(MessageThreadPool.class.getName());
	private final MessageStreamPair messageStreamPair;
	private final MessageExecutor messageExecutor;
	private final MessageReader messageReader;
	private final MessageWriter messageWriter;
	private final ErrorChecker errorChecker;

	/**
	 * Construct a MessageThreadPool whose threads will read from and write to the given MessageIO streams.
	 *
	 * @param messageStreamPair The MessageIO containing the streams to read/write.
	 */
	protected MessageThreadPool(MessageStreamPair messageStreamPair) {
		this.messageStreamPair = messageStreamPair;
		messageWriter = new MessageWriter(messageStreamPair.getOut());
		messageReader = new MessageReader(messageStreamPair.getIn());
		errorChecker = new ErrorChecker();
		messageExecutor = new MessageExecutor(this);
	}

	/**
	 * Set the context in which this thread pool should execute messages.
	 *
	 * @param context The context in which messages should be executed.
	 */
	protected void setMessageContext(MessageContext context) {
		messageExecutor.setMessageContext(context);
		messageWriter.setMessageContext(context);
	}

	/**
	 * Start the message reader thread managed by this connection.
	 */
	public void start() {
		messageReader.start();
		messageWriter.start();
		errorChecker.start();
		messageExecutor.start();
	}

	/**
	 * End this connection's execution loop and close any streams.
	 */
	public void shutdown() {
		LOGGER.log(Level.INFO, "Shutting down thread pool");
		messageWriter.stop();
		messageReader.stop();
		errorChecker.stop();
		messageExecutor.stop();
	}

	/**
	 * Get the MessageIO containing the keys and streams used by this connection.
	 *
	 * @return The MessageIO containing the keys and streams used by this connection.
	 */
	public MessageStreamPair getMessageStreamPair() { return messageStreamPair; }

	/**
	 * Queue up the supplied message to be written.
	 *
	 * @param message The message to be sent.
	 */
	public void enqueueMessage(Message message) { messageWriter.enqueue(message); }

	/**
	 * Get the messages received by the MessageReader since the last time this method was called.
	 *
	 * @return The messages received since the last time this method was called.
	 */
	protected Iterable<Message> getReceivedMessages() { return messageReader.getReceivedMessages(); }

	private class ErrorChecker
	{
		private final CheckErrorsTask checkErrorTask = new CheckErrorsTask();
		private final Timer checkErrorTimer = new Timer("Error Checker");

		public void start() { checkErrorTimer.scheduleAtFixedRate(checkErrorTask, 0, 10); }

		public void stop() { checkErrorTimer.cancel(); }
	}

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
