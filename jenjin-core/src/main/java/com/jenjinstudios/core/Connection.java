package com.jenjinstudios.core;

import com.jenjinstudios.core.concurrency.MessageThreadPool;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageStreamPair;

import java.io.InputStream;

/**
 * The Connection class utilizes a MessageInputStream and MessageOutputStream to read and write messages to another
 * connection.  This is done with four seperate Threads, started using the start() method; one for reading messages, one
 * for executing the retrieved messages' ExecutableMessage equivalent, one for writing messages, and one for monitoring
 * the others for errors.
 *
 * @author Caleb Brinkman
 */
public class Connection extends MessageThreadPool
{
	private final PingTracker pingTracker;
	private String name = "Connection";

	/**
	 * Construct a new connection using the given MessageIO for reading and writing messages.
	 *
	 * @param streams The MessageIO containing the input and output streams
	 */
	public Connection(MessageStreamPair streams) {
		super(streams, null);
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/core/io/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core XML Registry", stream);
		pingTracker = new PingTracker();
	}

	/**
	 * Get the PingTracker used by this connection to track latency.
	 *
	 * @return The PingTracker used by this connection to track latency.
	 */
	public PingTracker getPingTracker() { return pingTracker; }

	/**
	 * Get the name of this {@code Connection}.
	 *
	 * @return The name of this {@code Connection}.
	 */
	public String getName() { return name; }

	/**
	 * Set the name of this {@code Connection}.
	 *
	 * @param name The name of this {@code Connection}.
	 */
	public void setName(String name) { this.name = name; }
}
