package com.jenjinstudios.core;

import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;


public class MessageIO
{
	private final MessageInputStream in;
	private final MessageOutputStream out;

	public MessageIO(MessageInputStream in, MessageOutputStream out) {
		this.in = in;
		this.out = out;
	}

	public MessageInputStream getIn() { return in; }

	public MessageOutputStream getOut() { return out; }

}
