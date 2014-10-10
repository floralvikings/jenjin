package com.jenjinstudios.core;

import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;

/**
 * Used to contain a {@code MessageInputStream} and {@code MessageOutputStream}.
 *
 * @author Caleb Brinkman
 */
public class MessageIO
{
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

	public MessageInputStream getIn() { return in; }

	public MessageOutputStream getOut() { return out; }

}
