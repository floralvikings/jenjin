package com.jenjinstudios.core;

import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;

import java.io.IOException;
import java.security.PublicKey;
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

	/**
	 * Construct a new {@code MessageIO} from the given message input and output streams.
	 *
	 * @param in The input stream.
	 * @param out The output stream.
	 */
	public MessageIO(MessageInputStream in, MessageOutputStream out) {
		this.in = in;
		this.out = out;
	}

	/**
	 * Get the {@code MessageInputStream} managed by this {@code MessageIO}.
	 *
	 * @return The {@code MessageInputStream} managed by this {@code MessageIO}.
	 */
	MessageInputStream getIn() { return in; }

	/**
	 * Get the {@code MessageOutputStream} managed by this {@code MessageIO}.
	 *
	 * @return The {@code MessageOutputStream} managed by this {@code MessageIO}.
	 */
	MessageOutputStream getOut() { return out; }

	/**
	 * Set the public key used to encrypt relevant messages.
	 *
	 * @param publicKey The public key.
	 */
	public void setPublicKey(PublicKey publicKey) { getOut().setPublicKey(publicKey); }

	void closeOutputStream() {
		try
		{
			getOut().close();
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
