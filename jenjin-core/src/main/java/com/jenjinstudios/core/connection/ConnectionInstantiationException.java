package com.jenjinstudios.core.connection;

/**
 * Wraps any exceptions thrown when instantiating a Connection.
 *
 * @author Caleb Brinkman
 */
public class ConnectionInstantiationException extends Exception
{
	/**
	 * Construct a new ConnectionInstantiationException with the given cause.
	 *
	 * @param cause The cause of the exception.
	 */
	public ConnectionInstantiationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a new ConnectionInstantiationException with the given message.
	 *
	 * @param message The message.
	 */
	public ConnectionInstantiationException(String message) {
		super(message);
	}
}
