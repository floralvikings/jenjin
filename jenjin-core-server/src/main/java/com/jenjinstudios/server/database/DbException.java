package com.jenjinstudios.server.database;

/**
 * Thrown by a {@code DbTable} when there is a database error.
 *
 * @author Caleb Brinkman
 */
public class DbException extends Exception
{
	/**
	 * Construct a new DbException with the given message and cause.
	 *
	 * @param message The message.
	 * @param cause The cause.
	 */
	public DbException(String message, Throwable cause) { super(message, cause); }
}
