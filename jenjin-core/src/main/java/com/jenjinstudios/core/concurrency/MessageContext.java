package com.jenjinstudios.core.concurrency;

import com.jenjinstudios.core.io.Message;

import java.net.InetAddress;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Used to represent data that should be passed into an executable message on construction.
 *
 * @author Caleb Brinkman
 */
public class MessageContext
{
    private final PingTracker pingTracker = new PingTracker();
    private final LinkedList<Message> outgoing = new LinkedList<>();
	private String name;
	private InetAddress address;

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
