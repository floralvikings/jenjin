package com.jenjinstudios.server.net;

/**
 * @author Caleb Brinkman
 */
public class User
{
	/** The name of this user. */
	private String username;
	/** The hashed, salted password of this user. */
	private String password;

	/**
	 * Get the name of this user.
	 * @return The name of this user.
	 */
	public String getUsername() { return username; }

	/**
	 * Set the name of this user.
	 * @param username The new name.
	 */
	public void setUsername(String username) { this.username = username; }

	/**
	 * Get the hashed, salted password of this user.
	 * @return The hashed, salted password of this user.
	 */
	public String getPassword() { return password; }

	/**
	 * Set the hashed, salted password of this user.
	 * @param password The hashed, salted password of this user.
	 */
	public void setPassword(String password) { this.password = password; }
}
