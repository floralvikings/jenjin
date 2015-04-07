package com.jenjinstudios.core.concurrency;

import com.jenjinstudios.core.io.Message;

import java.net.InetAddress;
import java.security.Key;
import java.util.*;

/**
 * Used to represent data that should be passed into an executable message on construction.
 *
 * @author Caleb Brinkman
 */
public class MessageContext
{
	private final Map<InetAddress, Key> verifiedKeys;
	private final PingTracker pingTracker;
	private final LinkedList<Message> outgoing = new LinkedList<>();
	private String name;
	private InetAddress address;
	private Key encryptionKey;

	/**
	 * Construct a new MessageContext.
	 */
	public MessageContext() {
		this.pingTracker = new PingTracker();
		this.verifiedKeys = new HashMap<>(10);
	}

	/**
	 * Enqueue a message to be written to the output stream.
	 *
	 * @param message The message to add to the outgoing queue.
	 */
	public void enqueue(Message message) {
		synchronized (outgoing)
		{
			outgoing.add(message);
		}
	}

	/**
	 * Get the queued outgoing messages added since the last time this method was called.
	 *
	 * @return The messages queued since the last time this method was caled.
	 */
	public Deque<Message> getOutgoing() {
		Deque<Message> list = new LinkedList<>();
		synchronized (outgoing)
		{
			while (!outgoing.isEmpty())
			{
				list.add(outgoing.remove());
			}
		}
		return list;
	}

	/**
	 * Get the name of the context.
	 *
	 * @return The name of the context.
	 */
	public String getName() { return name; }

	/**
	 * Set the name of this context.
	 *
	 * @param name The new name.
	 */
	public void setName(String name) { this.name = name; }

	/**
	 * Get the PingTracker associated with this MessageContext.
	 *
	 * @return The PingTracker associated with this MessageContext.
	 */
	public PingTracker getPingTracker() { return pingTracker; }

	/**
	 * The key used to encrypt messages sent in this context.
	 *
	 * @return The key used to encrypt messages sent in this context.
	 */
	public Key getEncryptionKey() { return encryptionKey; }

	/**
	 * Set the key used to encrypt messages sent in this context.
	 *
	 * @param encryptionKey The key used to encrypt messages sent in this context.
	 */
	public void setEncryptionKey(Key encryptionKey) { this.encryptionKey = encryptionKey; }

	/**
	 * Get the (unmodifiable) map of internet addresses and keys with which they are associated that are verified to
	 * be correlated.
	 *
	 * @return The map of internet addresses and keys with which they are associated that are verified to be
	 * correlated.
	 */
	public Map<InetAddress, Key> getVerifiedKeys() { return Collections.unmodifiableMap(verifiedKeys); }

	/**
	 * Add a key to the map of verified keys, associated with the specified internet address.
	 *
	 * @param newAddress The internet address that will be using the specified key.
	 * @param key The key that will be used by the specified internet address.
	 */
	public void addVerifiedKey(InetAddress newAddress, Key key) {
		if (verifiedKeys.containsKey(newAddress))
		{
			throw new IllegalStateException("Internet address already has private key set.");
		}
		verifiedKeys.put(newAddress, key);
	}

	/**
	 * Get the internet address at the other end of this context.  May be null if no address is set.
	 *
	 * @return The inernet address at the other end of this context, null if unset.
	 */
	public InetAddress getAddress() { return address; }

	/**
	 * Set the internet address at the other end of this context.
	 *
	 * @param address The new address.
	 */
	public void setAddress(InetAddress address) { this.address = address; }
}
