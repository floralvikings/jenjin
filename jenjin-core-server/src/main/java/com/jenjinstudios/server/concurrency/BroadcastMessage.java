package com.jenjinstudios.server.concurrency;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Represents a message to be broadcast to multiple connections.
 *
 * @author Caleb Brinkman
 */
public class BroadcastMessage
{
	private final Message message;
	private final Collection<String> targets;

	/**
	 * Construct a new BroadcastMessage with the given message and targets.
	 *
	 * @param message The message.
	 * @param targets A collection of identifier strings specifying targets.
	 */
	public BroadcastMessage(Message message, Collection<String> targets) {
		this.message = MessageRegistry.getGlobalRegistry().cloneMessage(message);
		this.targets = new LinkedList<>(targets);
	}

	/**
	 * Construct a new BroadcastMessage with the given message, to br broadcast to all connections.
	 *
	 * @param message The message to be broadcast.
	 */
	public BroadcastMessage(Message message) { this(message, Collections.<String>emptyList()); }

	/**
	 * Get the Message to be broadcast.
	 *
	 * @return The Message to be broadcast.
	 */
	public Message getMessage() { return message; }

	/**
	 * Get the targets of this broadcast.
	 *
	 * @return The targets of this broadcast.
	 */
	public Collection<String> getTargets() { return Collections.unmodifiableCollection(targets); }
}
