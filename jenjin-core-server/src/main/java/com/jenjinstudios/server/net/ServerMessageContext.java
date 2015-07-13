package com.jenjinstudios.server.net;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.concurrency.BroadcastMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Context for messages executed by a connection on a server.
 *
 * @author Caleb Brinkman
 */
public class ServerMessageContext<T extends User> extends MessageContext
{
	private final Collection<BroadcastMessage> broadcasts = new ConcurrentLinkedQueue<>();
	private Authenticator<T> authenticator;
	private T user;
	private long loggedInTime;

	/**
	 * Get the user managed by this context.
	 *
	 * @return The user managed by this context.
	 */
	public T getUser() { return user; }

	/**
	 * Set the user managed by this context.
	 *
	 * @param user The new user.
	 */
	public void setUser(T user) { this.user = user; }

	/**
	 * Get the authenticator managed by this context.
	 *
	 * @return The authenticator managed by this context.
	 */
	public Authenticator<T> getAuthenticator() { return authenticator; }

	/**
	 * Set the authenticator managed by this context.
	 *
	 * @param authenticator The new authenticator.
	 */
	public void setAuthenticator(Authenticator<T> authenticator) { this.authenticator = authenticator; }

	/**
	 * Set the time at which the user managed by this context was logged in.
	 *
	 * @param loggedInTime The time, in millis (per System.currentTimeMillis).
	 */
	public void setLoggedInTime(long loggedInTime) { this.loggedInTime = loggedInTime; }

	/**
	 * Get the time at which the user managed by this context was logged in.
	 *
	 * @return The time in milliseconds from the epoch.
	 */
	public long getLoggedInTime() { return loggedInTime; }

	/**
	 * Get the messages that have been requested to be broadcast.
	 *
	 * @return The messages that have been requested to be broadcast.
	 */
	public Collection<BroadcastMessage> getBroadcasts() { return Collections.unmodifiableCollection(broadcasts); }

	/**
	 * Request to broadcast the given message.
	 *
	 * @param message The message to be broadcast.
	 */
	public void requestBroadcast(BroadcastMessage message) { broadcasts.add(message); }
}
