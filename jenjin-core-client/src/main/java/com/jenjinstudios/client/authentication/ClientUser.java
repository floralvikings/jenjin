package com.jenjinstudios.client.authentication;

/**
 * Used to represent a client-side user; contains fields for username and password.
 *
 * @author Caleb Brinkman
 */
public class ClientUser implements User
{
	private String username;
	private String password;

	@Override
	public String getUsername() { return username; }

	@Override
	public String getPassword() { return password; }

	@Override
	public void setUsername(String username) { this.username = username; }

	@Override
	public void setPassword(String password) { this.password = password; }
}
