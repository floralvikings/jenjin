package com.jenjinstudios.server.database;

/**
 * Represents an exception when accessing the database; typically used as a wrapper for database-specific exceptions.
 *
 * @author Caleb Brinkman
 */
public class DatabaseException extends Exception
{
	/**
	 * Construct a new DatabaseException with the given cause.
	 *
	 * @param cause The cause of the exception.
	 */
	public DatabaseException(Throwable cause) { super(cause); }

	/**
	 * Construct a new DatabaseException with the given message.
	 *
	 * @param message The message.
	 */
	public DatabaseException(String message) { super(message); }

	/**
	 * Construct a new DatabaseException with the given message and cause.
	 *
	 * @param message The message.
	 * @param cause The cause.
	 */
	public DatabaseException(String message, Throwable cause) { super(message, cause); }
}
