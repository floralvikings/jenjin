package com.jenjinstudios.message;

import com.jenjinstudios.io.BaseMessage;

/**
 * Message from a client requesting to log out.
 *
 * @author Caleb Brinkman
 */
public class LogoutRequest extends BaseMessage
{
	/** The message registration id of this message type. */
	public static final short ID = 3;

	/** Construct a new LogoutRequest. */
	public LogoutRequest()
	{
		super(ID);
	}
}
