package com.jenjinstudios.client.authentication;

/**
 * Represents a user with a username and password.
 *
 * @author Caleb Brinkman
 */
public interface User
{
	/**
	 * Get the username of this user.
	 *
	 * @return The username of this user.
	 */
	String getUsername();

	/**
	 * Get the password of this user.
	 *
	 * @return The password of this user.
	 */
	String getPassword();
}
