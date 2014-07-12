package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;

/**
 * The {@code AuthClient} class is a {@code Client} with the ability to store user information and attempt to log into a
 * {@code Server} using a username and password.
 * @author Caleb Brinkman
 */
public class AuthClient extends Client
{
	private final ClientUser user;
	/** Whether the user is logged in. */
	private boolean loggedIn;
	/** The time at which this client was successfully logged in. */
	private long loggedInTime;

	public AuthClient(MessageIO messageIO, ClientUser user) {
		super(messageIO);
		this.user = user;
	}

	/**
	 * Get whether this client is logged in.
	 * @return true if this client has received a successful LoginResponse
	 */
	public boolean isLoggedIn() { return loggedIn; }

	/**
	 * Set whether this client is logged in.
	 * @param l Whether this client is logged in.
	 */
	public void setLoggedIn(boolean l) { loggedIn = l; }

	/**
	 * Get the username of this client.
	 * @return The username of this client.
	 */
	public ClientUser getUser() { return user; }

	/**
	 * Get the time at which this client was successfully logged in.
	 * @return The time of the start of the server cycle during which this client was logged in.
	 */
	public long getLoggedInTime() { return loggedInTime; }

	/**
	 * Set the logged in time for this client.
	 * @param loggedInTime The logged in time for this client.
	 */
	public void setLoggedInTime(long loggedInTime) { this.loggedInTime = loggedInTime; }

}
