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
	private final PingTracker pingTracker;
	private String name;
	private InetAddress address;
	private Key encryptionKey;

	/**
	 * Construct a new SimpleMessageContext.
	 */
	public SimpleMessageContext() {
		this.pingTracker = new PingTracker();
		this.verifiedKeys = new HashMap<>(10);
	}

	/**
	 * Construct a new MessageContext with the given name, communicating with the given internet address.
	 *
	 * @param name The name of the context.
	 * @param address The internet address of the "other end" of this context.
	 */
	public SimpleMessageContext(String name, InetAddress address) {
		this();
		this.name = name;
		this.address = address;
	}

	@Override
	public String getName() { return name; }

	@Override
	public void setName(String name) { this.name = name; }

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

	@Override
	public void addVerifiedKey(InetAddress newAddress, Key key) {
		if (verifiedKeys.containsKey(newAddress))
		{
			throw new IllegalStateException("Internet address already has private key set.");
		}
		verifiedKeys.put(newAddress, key);
	}

	@Override
	public InetAddress getAddress() { return address; }

	@Override
	public void setAddress(InetAddress address) { this.address = address; }
}
