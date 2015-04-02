package com.jenjinstudios.core.concurrency;

import java.net.InetAddress;
import java.security.Key;
import java.util.Map;

/**
 * Used to represent data that should be passed into an executable message on construction.
 *
 * @author Caleb Brinkman
 */
public interface MessageContext
{
	/**
	 * Get the name of the context.
	 *
	 * @return The name of the context.
	 */
	String getName();

	/**
	 * The key used to encrypt messages sent in this context.
	 *
	 * @return The key used to encrypt messages sent in this context.
	 */
	Key getEncryptionKey();

	/**
	 * Set the key used to encrypt messages sent in this context.
	 *
	 * @param encryptionKey The key used to encrypt messages sent in this context.
	 */
	void setEncryptionKey(Key encryptionKey);

	/**
	 * Get the map of internet addresses and keys with which they are associated that are verified to be correlated.
	 *
	 * @return The map of internet addresses and keys with which they are associated that are verified to be
	 * correlated.
	 */
	Map<InetAddress, Key> getVerifiedKeys();
}
