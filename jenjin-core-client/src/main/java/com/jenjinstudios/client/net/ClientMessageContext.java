package com.jenjinstudios.client.net;

import com.jenjinstudios.core.SimpleMessageContext;

import java.net.InetAddress;

/**
 * Message context in which messages received by a {@code Client} should execute.
 *
 * @author Caleb Brinkman
 */
public class ClientMessageContext extends SimpleMessageContext
{
	private final LoginTracker loginTracker = new LoginTracker();

	/**
	 * Construct a ClientMessageContext with the specified name.
	 * @param name The name of the context.
	 */
	public ClientMessageContext(String name) { super(name); }

	/**
	 * Construct a ClientMessageContext with the specified name and internet address.
	 * @param name The name of the context.
	 * @param address The address of the context.
	 */
	public ClientMessageContext(String name, InetAddress address) { super(name, address); }

	/**
	 * Get the login tracker maintained in this context.
	 *
	 * @return The login tracker maintained in this context.
	 */
	public LoginTracker getLoginTracker() { return loginTracker; }
}
