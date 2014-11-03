package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;

import java.io.IOException;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to contain a {@code MessageInputStream} and {@code MessageOutputStream}.
 *
 * @author Caleb Brinkman
 */
public class MessageIO
{
	private static final Logger LOGGER = Logger.getLogger(MessageIO.class.getName());
	private final MessageInputStream in;
	private final MessageOutputStream out;
	private final LinkedList<Message> outgoingMessages;

	/**
	 * Construct a new {@code MessageIO} from the given message input and output streams.
	 *
	 * @param in The input stream.
	 * @param out The output stream.
	 */
	public MessageIO(MessageInputStream in, MessageOutputStream out) {
		this.in = in;
		this.out = out;
		outgoingMessages = new LinkedList<>();
	}

	/**
	 * Get the {@code MessageInputStream} managed by this {@code MessageIO}.
	 *
	 * @return The {@code MessageInputStream} managed by this {@code MessageIO}.
	 */
	MessageInputStream getIn() { return in; }

	/**
	 * Set the public key used to encrypt relevant messages.
	 *
	 * @param publicKey The public key.
	 */
	public void setPublicKey(PublicKey publicKey) { out.setPublicKey(publicKey); }

	/**
	 * Add the specified {@code Message} to the queue of outgoing messages.  This queue is written when {@code
	 * writeAllMessages} is called.
	 *
	 * @param message The {@code Message} to write.
	 */
	public void queueOutgoingMessage(Message message) {
		if (out.isClosed())
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
	 *
	 * @throws java.io.IOException If there is an exception writing a message to the output stream.
	 */
	public void writeAllMessages() throws IOException {
		synchronized (outgoingMessages)
		{
			while (!outgoingMessages.isEmpty())
			{
				writeMessage(outgoingMessages.remove());
			}
		}
	}

	void writeMessage(Message o) throws IOException { out.writeMessage(o); }

	void closeOutputStream() {
		try
		{
			out.close();
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, "Error closing output stream.", e);
		}
	}

	void closeInputStream() {
		try
		{
			getIn().close();
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, "Error closing input stream.", e);
		}
	}
}
