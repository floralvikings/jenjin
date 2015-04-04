package com.jenjinstudios.client.net;

import com.jenjinstudios.core.SimpleMessageContext;

/**
 * Message context in which messages received by a {@code Client} should execute.
 *
 * @author Caleb Brinkman
 */
public class ClientMessageContext extends SimpleMessageContext
{
	private final LoginTracker loginTracker = new LoginTracker();

	/**
	 * Get the login tracker maintained in this context.
	 *
	 * @return The login tracker maintained in this context.
	 */
	public LoginTracker getLoginTracker() { return loginTracker; }
}
