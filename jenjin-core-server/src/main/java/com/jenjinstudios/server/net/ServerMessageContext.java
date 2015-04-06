package com.jenjinstudios.server.net;

import com.jenjinstudios.core.SimpleMessageContext;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.User;

/**
 * Context for messages executed by a connection on a server.
 *
 * @author Caleb Brinkman
 */
public class ServerMessageContext<T extends User> extends SimpleMessageContext
{
	private Authenticator<T> authenticator;
	private User user;
	private long loggedInTime;

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
	public Authenticator<T> getAuthenticator() { return authenticator; }

	/**
	 * Set the authenticator managed by this context.
	 *
	 * @param authenticator The new authenticator.
	 */
	public void setAuthenticator(Authenticator<T> authenticator) { this.authenticator = authenticator; }

	/**
	 * Set the time at which the user managed by this context was logged in.
	 *
	 * @param loggedInTime The time, in millis (per System.currentTimeMillis).
	 */
	public void setLoggedInTime(long loggedInTime) { this.loggedInTime = loggedInTime; }

	/**
	 * Get the time at which the user managed by this context was logged in.
	 *
	 * @return The time in milliseconds from the epoch.
	 */
	public long getLoggedInTime() { return loggedInTime; }
}
