package com.jenjinstudios.core;

import com.jenjinstudios.core.concurrency.MessageContext;

import java.net.InetAddress;
import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the most basic implemented MessageContext.
 *
 * @author Caleb Brinkman
 */
public class SimpleMessageContext implements MessageContext
{
	private final Map<InetAddress, Key> verifiedKeys;
	private final String name;
	private final PingTracker pingTracker;
	private Key encryptionKey;

	/**
	 * Construct a new MessageContext with the given name.
	 *
	 * @param name The name of the context.
	 */
	public SimpleMessageContext(String name) {
		this.name = name;
		this.pingTracker = new PingTracker();
		this.verifiedKeys = new HashMap<>(10);
	}

	@Override
	public String getName() { return name; }

	/**
	 * Get the PingTracker associated with this MessageContext.
	 *
	 * @return The PingTracker associated with this MessageContext.
	 */
	public PingTracker getPingTracker() { return pingTracker; }

	@Override
	public Key getEncryptionKey() { return encryptionKey; }

	@Override
	public void setEncryptionKey(Key encryptionKey) { this.encryptionKey = encryptionKey; }

	@Override
	public Map<InetAddress, Key> getVerifiedKeys() { return Collections.unmodifiableMap(verifiedKeys); }
}
