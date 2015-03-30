package com.jenjinstudios.core.io;

import java.net.InetAddress;

/**
 * Used to contain a {@code MessageInputStream} and {@code MessageOutputStream}.
 *
 * @author Caleb Brinkman
 */
public class MessageStreamPair
{
	private final MessageInputStream in;
	private final MessageOutputStream out;
    private final InetAddress address;

	/**
	 * Construct a new {@code MessageIO} from the given message input and output streams.
     *
     * @param in The input stream.
     * @param out The output stream.
     */
	public MessageStreamPair(MessageInputStream in, MessageOutputStream out) { this(in, out, null); }

    /**
     * Construct a new {@code MessageIO} from the given message input and output streams.
     *
     * @param in The input stream.
     * @param out The output stream.
     * @param address The Internet Address of the complementary connection.
     */
	public MessageStreamPair(MessageInputStream in, MessageOutputStream out, InetAddress address) {
		this.in = in;
        this.out = out;
        this.address = address;
    }

    /**
     * Get the address of the complementary connection, if it exists.  Returns null if no address is known.
     *
     * @return The address of the complementary connection, null if unknown.
     */
    public InetAddress getAddress() { return address; }

    /**
     * Get the {@code MessageInputStream} managed by this {@code MessageIO}.
     *
     * @return The {@code MessageInputStream} managed by this {@code MessageIO}.
     */
	public MessageInputStream getIn() { return in; }

    /**
     * Get the {@code MessageOutputStream} managed by this {@code MessageIO}.
     *
     * @return The {@code MessageOutputStream} managed by this {@code MessageIO}.
     */
    public MessageOutputStream getOut() { return out; }

}
