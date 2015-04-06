package com.jenjinstudios.server.net;

import com.jenjinstudios.core.SimpleMessageContext;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.User;

/**
 * Context for messages executed by a connection on a server.
 *
 * @author Caleb Brinkman
 */
public class ServerMessageContext extends SimpleMessageContext
{
	private Authenticator authenticator;
	private User user;

	/**
	 * Get the user managed by this context.
	 *
	 * @return The user managed by this context.
	 */
	public User getUser() { return user; }

	/**
	 * Set the user managed by this context.
	 *
	 * @param user The new user.
	 */
	public void setUser(User user) { this.user = user; }

	/**
	 * Get the authenticator managed by this context.
	 *
	 * @return The authenticator managed by this context.
	 */
	public Authenticator getAuthenticator() { return authenticator; }

	/**
	 * Set the authenticator managed by this context.
	 *
	 * @param authenticator The new authenticator.
	 */
	public void setAuthenticator(Authenticator authenticator) { this.authenticator = authenticator; }
}
