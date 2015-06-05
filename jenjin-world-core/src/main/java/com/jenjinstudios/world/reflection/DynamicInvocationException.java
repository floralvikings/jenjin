package com.jenjinstudios.world.reflection;

/**
 * Thrown when invoking a dynamic method.
 *
 * @author Caleb Brinkman
 */
public class DynamicInvocationException extends Exception
{
	/**
	 * Construct a new DynamicInvocationException with the given message.
	 *
	 * @param message The message.
	 */
	public DynamicInvocationException(String message) { super(message); }

	/**
	 * Construct a new DynamicInvocationException with the given message and cause.
	 *
	 * @param message The message.
	 * @param cause The cause.
	 */
	public DynamicInvocationException(String message, Throwable cause) { super(message, cause); }

	/**
	 * Construct a new DynamicInvocationException with the given cause.
	 *
	 * @param cause The cause.
	 */
	public DynamicInvocationException(Throwable cause) { super(cause); }
}
