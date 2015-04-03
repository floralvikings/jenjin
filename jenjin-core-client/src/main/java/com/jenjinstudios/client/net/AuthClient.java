package com.jenjinstudios.client.net;

import com.jenjinstudios.core.io.MessageStreamPair;

/**
 * The {@code AuthClient} class is a {@code Client} with the ability to store user information and attempt to log into a
 * {@code Server} using a username and password.
 *
 * @author Caleb Brinkman
 */
public class AuthClient extends Client
{

	/**
	 * Construct a new client with authentication abilities.
     *
     * @param messageStreamPair The MessageIO used by this client to communicate with a server.
     * @param user The user which this client will attempt to authenticate.
     */
	public AuthClient(MessageStreamPair messageStreamPair, ClientUser user) { super(messageStreamPair, user); }

}
