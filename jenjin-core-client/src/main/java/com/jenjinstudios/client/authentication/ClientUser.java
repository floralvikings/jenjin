package com.jenjinstudios.client.authentication;

/**
 * Used to represent a client-side user; contains fields for username and password.
 *
 * @author Caleb Brinkman
 */
public class ClientUser implements User
{
	private final String username;
	private final String password;

    /**
     * Construct a new ClientUser with the given username and password.
     *
     * @param username The client's username.
     * @param password The client's password.
     */
    public ClientUser(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public String getUsername() { return username; }

	@Override
	public String getPassword() { return password; }
}
