package com.jenjinstudios.client.net;

import com.jenjinstudios.client.authentication.User;
import com.jenjinstudios.core.concurrency.MessageContext;

/**
 * Message context in which messages received by a {@code Client} should execute.
 *
 * @author Caleb Brinkman
 */
public class ClientMessageContext extends MessageContext
{
	private final LoginTracker loginTracker = new LoginTracker();
	private User user;

	/**
	 * Get the login tracker maintained in this context.
	 *
	 * @return The login tracker maintained in this context.
	 */
	public LoginTracker getLoginTracker() { return loginTracker; }

	/**
	 * Get the user handled in this context.
	 *
	 * @return The user handled in this context.
	 */
	public User getUser() { return user; }

	/**
	 * Set the user to be handled by this context.
	 *
	 * @param user The user to be handled by this context.
	 */
	public void setUser(User user) { this.user = user; }
}
