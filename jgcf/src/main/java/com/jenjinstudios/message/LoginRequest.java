package com.jenjinstudios.message;

import com.jenjinstudios.io.BaseMessage;

/**
 * The {@code LoginRequest} is used to send username and password information.
 *
 * @author Caleb Brinkman
 */
public class LoginRequest extends BaseMessage
{
	/** The registration ID of this message type. */
	public static final short ID = 1;
	/** The username of the user to be logged in. */
	public final String username;
	/** The password of the user to be logged in. */
	public final String password;

	/**
	 * Construct a new LoginRequest with the given username and password.
	 *
	 * @param username The username.
	 * @param password The password.
	 */
	public LoginRequest(String username, String password)
	{
		super(username, password);
		this.username = username;
		this.password = password;
	}

	@Override
	public short getID()
	{
		return ID;
	}
}
