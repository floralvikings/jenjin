package com.jenjinstudios.core.concurrency;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles a thread which reads messages from the given input stream.  When a message is read, the
 * appropriate executable message is onstructed and placed into the executable message queue.  It should be noted that
 * the queue is not thread safe, and must be locked on if its contents are to be modified.
 *
 * @author Caleb Brinkman
 */
public class MessageReader
{
	private static final Logger LOGGER = Logger.getLogger(MessageReader.class.getName());
	private final Deque<Message> incoming;
	private final MessageInputStream inputStream;
	private final Timer runTimer;
	private final ReadTask readTask;
	private volatile boolean errored;

	/**
	 * Construct a new MessageReader that will read from the given MessageInputStream.
	 *
	 * @param inputStream The input stream from which this reader will read.
	 */
	public MessageReader(MessageInputStream inputStream) {
		this.inputStream = inputStream;
		incoming = new LinkedList<>();
		runTimer = new Timer("MessageReader");
		readTask = new ReadTask();
	}

	/**
	 * Returns true if the timer reading messages has encountered an error, and not run again successfully since then.
	 *
	 * @return True if the timer reading messages has encountered an error, and not run again successfully since then.
	 */
	public boolean isErrored() { return errored; }

	/**
	 * Begin reading messages from the output stream.
	 */
	public void start() { runTimer.schedule(readTask, 0, 10); }

	/**
	 * Stop reading messages from the output stream.  Once this has been called, the timer may not be restarted.
	 */
	public void stop() { runTimer.cancel(); }

	/**
	 * Returns a list of all messages received since the last time this method was called.
	 *
	 * @return A list of all messages received since the last time this method was called.
	 */
	public Deque<Message> getReceivedMessages() {
		LinkedList<Message> messages = new LinkedList<>();
		synchronized (incoming)
		{
			while (!incoming.isEmpty())
			{
				messages.add(incoming.remove());
			}
		}
		return messages;
	}

	private class ReadTask extends TimerTask
	{
		@Override
		public void run() {
			try
			{
				Message message = inputStream.readMessage();
				synchronized (incoming)
				{
					incoming.add(message);
				}
				errored = false;
			} catch (IOException e)
			{
				if (!errored)
				{    // Only log the error once.
					LOGGER.log(Level.WARNING, "MessageReader encountered an error while reading message", e);
				}
				errored = true;
			}
		}
	}

}
