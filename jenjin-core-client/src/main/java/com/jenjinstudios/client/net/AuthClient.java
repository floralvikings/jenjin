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
	private final LoginTracker loginTracker;

	public AuthClient(MessageIO messageIO, ClientUser user) {
		super(messageIO);
		this.loginTracker = new LoginTracker(this);
		this.user = user;
	}

	public LoginTracker getLoginTracker() { return loginTracker; }

	/**
	 * Get the username of this client.
	 * @return The username of this client.
	 */
	public ClientUser getUser() { return user; }

}
