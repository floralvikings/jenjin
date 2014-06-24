package com.jenjinstudios.server.net;

/**
 * @author Caleb Brinkman
 */
public class User
{
	private String salt;
	private boolean loggedIn;
	private String username;
	/** The hashed, salted password of this user. */
	private String password;

	public String getUsername() { return username; }

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

	public String getSalt() { return salt; }

	public void setSalt(String salt) { this.salt = salt; }

	public boolean isLoggedIn() { return loggedIn; }

	public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }
}
