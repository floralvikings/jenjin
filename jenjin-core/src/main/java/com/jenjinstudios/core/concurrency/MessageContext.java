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
	 * Get the (unmodifiable) map of internet addresses and keys with which they are associated that are verified to
	 * be correlated.
	 *
	 * @return The map of internet addresses and keys with which they are associated that are verified to be
	 * correlated.
	 */
	Map<InetAddress, Key> getVerifiedKeys();

	/**
	 * Add a key to the map of verified keys, associated with the specified internet address.
	 *
	 * @param address The internet address that will be using the specified key.
	 * @param key The key that will be used by the specified internet address.
	 */
	void addVerifiedKey(InetAddress address, Key key);
}
