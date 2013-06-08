package com.jenjinstudios.message;

import com.jenjinstudios.io.BaseMessage;

/**
 * The server response to a logout request.
 *
 * @author Caleb Brinkman
 */
public class LogoutResponse extends BaseMessage
{
	/** The message registration ID of this message type. */
	public static final short ID = 4;
	/** Flags whether the logout was successful. */
	public final boolean SUCCESS;

	/**
	 * Construct a new LogoutResponse.
	 *
	 * @param success The success of the logout.
	 */
	public LogoutResponse(Boolean success)
	{
		super(success);
		SUCCESS = success;
	}

	@Override
	public short getID()
	{
		return ID;
	}
}
