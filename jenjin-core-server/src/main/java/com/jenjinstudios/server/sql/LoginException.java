package com.jenjinstudios.server.sql;

import java.sql.SQLException;

/**
 * @author Caleb Brinkman
 */
public class LoginException extends Exception
{
	public LoginException(String message) {
		super(message);
	}

	public LoginException(String s, SQLException e) {
		super(s, e);
	}
}
