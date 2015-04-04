package com.jenjinstudios.client.authentication;

/**
 * Used to represent a client-side user; contains fields for username and password.
 *
 * @author Caleb Brinkman
 */
public class ClientUser
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

    /**
     * Get the username of this user.
     *
     * @return The username of this user.
     */
    public String getUsername() { return username; }

    /**
     * Get the password of this user.
     *
     * @return The password of this user.
     */
    public String getPassword() { return password; }
}
