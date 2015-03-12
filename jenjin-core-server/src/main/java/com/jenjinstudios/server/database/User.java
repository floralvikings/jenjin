package com.jenjinstudios.server.database;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Caleb Brinkman
 */
public class User
{
	/** The Hash table of custom properties of this user. */
	private final Map<String, Object> properties = new HashMap<>();
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

	public Map<String, Object> getProperties() { return properties; }
}
