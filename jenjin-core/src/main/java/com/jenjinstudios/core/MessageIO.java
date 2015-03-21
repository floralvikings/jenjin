package com.jenjinstudios.core;

import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to contain a {@code MessageInputStream} and {@code MessageOutputStream}.
 *
 * @author Caleb Brinkman
 */
public class MessageIO
{
    private static final Logger LOGGER = Logger.getLogger(MessageIO.class.getName());
    private final MessageInputStream in;
    private final MessageOutputStream out;
    private final InetAddress address;

	/**
	 * Construct a new {@code MessageIO} from the given message input and output streams.
     *
     * @param in The input stream.
     * @param out The output stream.
     */
    public MessageIO(MessageInputStream in, MessageOutputStream out) { this(in, out, null); }

    /**
     * Construct a new {@code MessageIO} from the given message input and output streams.
     *
     * @param in The input stream.
     * @param out The output stream.
     * @param address The Internet Address of the complementary connection.
     */
    public MessageIO(MessageInputStream in, MessageOutputStream out, InetAddress address) {
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
    MessageInputStream getIn() { return in; }

    /**
     * Get the {@code MessageOutputStream} managed by this {@code MessageIO}.
     *
     * @return The {@code MessageOutputStream} managed by this {@code MessageIO}.
     */
    public MessageOutputStream getOut() { return out; }

	void closeOutputStream() {
		try
        {
            out.close();
        } catch (IOException e)
        {
            LOGGER.log(Level.INFO, "Error closing output stream.", e);
        }
    }

    void closeInputStream() {
        try
        {
            in.close();
        } catch (IOException e)
        {
            LOGGER.log(Level.INFO, "Error closing input stream.", e);
        }
    }
}
