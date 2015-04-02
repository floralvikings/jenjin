package com.jenjinstudios.core.concurrency;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageOutputStream;

import java.io.IOException;
import java.security.Key;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The MessageWriter class handles a timer which attempts to write all enqueued messages once every 10 milliseconds.
 * This timer can be started by calling the {@code start} method. If an IO error is encountered during the message
 * writes, writing is halted and the {@code isErrored} method will return true.  The timer will continue to attempt to
 * run until {@code stop} is called, and if it runs again successfully, {@code isErrored} will return false.
 *
 * @author Caleb Brinkman
 */
public class MessageWriter
{
	private static final Logger LOGGER = Logger.getLogger(MessageWriter.class.getName());
	private final LinkedList<Message> outgoing;
	private final MessageOutputStream outputStream;
	private final Timer runTimer;
	private final WriteTask writeTask;
	private MessageContext context;
	private volatile boolean errored;

	/**
	 * Construct a MessageWriter that will write messages to the given MessageOutputStream.
	 *
	 * @param outputStream The output stream to which this writer will write.
	 */
	public MessageWriter(MessageOutputStream outputStream) {
		this.outputStream = outputStream;
		outgoing = new LinkedList<>();
		runTimer = new Timer("MessageWriter");
		writeTask = new WriteTask();
	}

	/**
	 * Returns true if the timer writing messages has encountered and error, and not run again successfully since then.
	 *
	 * @return True if the timer writing messages has encountered and error, and not run again successfully since then.
	 */
	public boolean isErrored() { return errored; }

	/**
	 * Begin writing messages to the output stream.
	 */
	public void start() { runTimer.scheduleAtFixedRate(writeTask, 0, 10); }

	/**
	 * Stop writing messages to the output stream.  Once this has been called, the timer may not be restarted.
	 */
	public void stop() {
		runTimer.cancel();
		try
		{
			outputStream.close();
		} catch (IOException e)
		{
			LOGGER.log(Level.FINE, "Exception when closing output stream", e);
		}
	}

	/**
	 * Enqueue a message to be written to the output stream.
	 *
	 * @param message The message to add to the outgoing queue.
	 */
	public void enqueue(Message message) {
		synchronized (outgoing)
		{
			outgoing.add(message);
		}
	}

	/**
	 * Set the message context in which messages should be written.
	 *
	 * @param messageContext The context in which messages should be written.
	 */
	public void setMessageContext(MessageContext messageContext) { this.context = messageContext; }

	private class WriteTask extends TimerTask
	{
		@Override
		public void run() {
			Key encryptionKey = (context != null) ? context.getEncryptionKey() : null;
			synchronized (outgoing)
			{
				boolean noErrorThisExecution = true;
				while (!outgoing.isEmpty() && noErrorThisExecution)
				{
					Message message = outgoing.removeFirst();
					try
					{
						if (encryptionKey != null)
						{
							outputStream.writeMessage(message, encryptionKey);
						} else
						{
							outputStream.writeMessage(message);
						}
						errored = false;
					} catch (IOException e)
					{
						LOGGER.log(Level.WARNING, "MessageWriter encountered an error while writing message", e);
						outgoing.addFirst(message);
						errored = true;
						noErrorThisExecution = false;
					}
				}
			}
		}
	}
}
