package com.jenjinstudios.core;

import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;

/**
 * @author Caleb Brinkman
 */
public class MessageIO
{
	private final MessageInputStream in;
	private final MessageOutputStream out;
	private final MessageRegistry mr;

	public MessageIO(MessageInputStream in, MessageOutputStream out, MessageRegistry mr) {
		this.in = in;
		this.out = out;
		this.mr = mr;
	}

	public MessageInputStream getIn() { return in; }

	public MessageOutputStream getOut() { return out; }

	public MessageRegistry getMr() { return mr; }
}
