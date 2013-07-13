package com.jenjinstudios.message;

import com.jenjinstudios.io.BaseMessage;

/**
 * The response sent by the server indicating the success or failure of a login request.
 *
 * @author Caleb Brinkman
 */
public class LoginResponse extends BaseMessage
{
	/** The message registration ID of this message type. */
	public static final short ID = 2;
	/** Flags whether the login attempt was successful. */
	public final boolean SUCCESS;
	/** Indicates the time of the start of the server cycle in which the client was successfully logged in. */
	public final long LOGIN_TIME;

	/**
	 * Construct a new LoginResponse.
	 *
	 * @param success   The success of the login.
	 * @param loginTime The time of login.
	 */
	public LoginResponse(Boolean success, Long loginTime)
	{
		super(ID, success, loginTime);
		SUCCESS = success;
		LOGIN_TIME = loginTime;
	}
}
