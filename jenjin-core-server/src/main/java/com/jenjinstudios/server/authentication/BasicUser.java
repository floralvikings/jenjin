package com.jenjinstudios.server.authentication;

/**
 * Represents the most basic fields needed to fully implement the User interface.
 *
 * @author Caleb Brinkman
 */
public class BasicUser implements User
{
	private final String username;
	private String salt;
	private boolean loggedIn;
	/** The hashed, salted password of this user. */
	private String password;

	/**
	 * Cosntruct a new BasicUser with the given user name.
	 *
	 * @param username The username.
	 */
	public BasicUser(String username) { this.username = username; }

	@Override
	public String getUsername() { return username; }

	@Override
	public String getPassword() { return password; }

	@Override
	public void setPassword(String password) { this.password = password; }

	@Override
	public String getSalt() { return salt; }

	@Override
	public void setSalt(String salt) { this.salt = salt; }

	@Override
	public boolean isLoggedIn() { return loggedIn; }

	@Override
	public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }
}
