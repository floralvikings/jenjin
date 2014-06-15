package com.jenjinstudios.io;

import java.io.IOException;

/**
 * @author Caleb Brinkman
 */
public class MessageTypeException extends IOException
{
	private short id;

	public MessageTypeException(short id) {
		super("Message " + id + " not registered.");
		this.id = id;
	}

	public short getId() { return id; }
}
