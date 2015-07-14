package com.jenjinstudios.server.net;

import com.jenjinstudios.core.connection.ConnectionConfig;

import java.security.KeyPair;

/**
 * Used for configuring a Server.
 *
 * @author Caleb Brinkman
 */
public class ServerConfig<T extends ServerMessageContext> extends ConnectionConfig<T>
{
	private KeyPair keyPair;
	private int ups;

	/**
	 * Get the KeyPair used for encryption and decryption.
	 *
	 * @return The KeyPair used for encryption and decryption.
	 */
	public KeyPair getKeyPair() { return keyPair; }

	/**
	 * Get the updates per second at which the server should update.
	 *
	 * @return The updates per second at which the server should update.
	 */
	public int getUps() { return ups; }
}
