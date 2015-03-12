package com.jenjinstudios.server.database;

/**
 * @author Caleb Brinkman
 */
public class User implements IUser
{
	private String salt;
	private boolean loggedIn;
	private String username;
	/** The hashed, salted password of this user. */
	private String password;

	@Override
	public String getUsername() { return username; }

	@Override
	public void setUsername(String username) { this.username = username; }

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
