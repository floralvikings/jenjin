package com.jenjinstudios.core.concurrency;

import com.jenjinstudios.core.PingTracker;

import java.net.InetAddress;
import java.security.Key;
import java.util.Map;

/**
 * Used to represent data that should be passed into an executable message on construction.
 *
 * @author Caleb Brinkman
 */
public abstract class MessageContext
{
	/**
	 * Get the name of the context.
	 *
	 * @return The name of the context.
	 */
	public abstract String getName();

	/**
	 * Set the name of this context.
	 *
	 * @param name The new name.
	 */
	public abstract void setName(String name);

	/**
	 * Get the PingTracker associated with this MessageContext.
	 *
	 * @return The PingTracker associated with this MessageContext.
	 */
	public abstract PingTracker getPingTracker();

	/**
	 * The key used to encrypt messages sent in this context.
	 *
	 * @return The key used to encrypt messages sent in this context.
	 */
	public abstract Key getEncryptionKey();

	/**
	 * Set the key used to encrypt messages sent in this context.
	 *
	 * @param encryptionKey The key used to encrypt messages sent in this context.
	 */
	public abstract void setEncryptionKey(Key encryptionKey);

	/**
	 * Get the (unmodifiable) map of internet addresses and keys with which they are associated that are verified to
	 * be correlated.
	 *
	 * @return The map of internet addresses and keys with which they are associated that are verified to be
	 * correlated.
	 */
	public abstract Map<InetAddress, Key> getVerifiedKeys();

	/**
	 * Add a key to the map of verified keys, associated with the specified internet address.
	 *
	 * @param newAddress The internet address that will be using the specified key.
	 * @param key The key that will be used by the specified internet address.
	 */
	public abstract void addVerifiedKey(InetAddress newAddress, Key key);

	/**
	 * Get the internet address at the other end of this context.  May be null if no address is set.
	 *
	 * @return The inernet address at the other end of this context, null if unset.
	 */
	public abstract InetAddress getAddress();

	/**
	 * Set the internet address at the other end of this context.
	 *
	 * @param address The new address.
	 */
	public abstract void setAddress(InetAddress address);
}
