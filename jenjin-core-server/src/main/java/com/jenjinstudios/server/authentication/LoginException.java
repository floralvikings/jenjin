package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.DbException;

import java.sql.SQLException;

/**
 * Thrown when an exception occurs during a login process.
 *
 * @author Caleb Brinkman
 */
public class LoginException extends DbException
{
	/**
	 * Construct a LoginException with the given message.
	 *
	 * @param message The message.
	 */
	public LoginException(String message) { super(message); }

	/**
	 * Construct a LoginException with the given message and cause.
	 *
	 * @param s The mesasge.
	 * @param e The cause.
	 */
	public LoginException(String s, SQLException e) { super(s, e); }
}
