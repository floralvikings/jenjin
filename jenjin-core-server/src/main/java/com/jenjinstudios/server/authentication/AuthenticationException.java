package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.DbException;

/**
 * Thrown when an exception occurs during a login process.
 *
 * @author Caleb Brinkman
 */
public class AuthenticationException extends DbException
{
	/**
	 * Construct a LoginException with the given message.
	 *
	 * @param message The message.
	 */
	public AuthenticationException(String message) { super(message); }

	/**
	 * Construct a LoginException with the given message and cause.
	 *
	 * @param s The mesasge.
	 * @param e The cause.
	 */
	public AuthenticationException(String s, Throwable e) { super(s, e); }
}
