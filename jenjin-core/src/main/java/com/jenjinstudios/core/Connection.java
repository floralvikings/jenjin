package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.message.MessageFactory;

import java.net.InetAddress;
import java.security.Key;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Connection} class is a subclass of the {@code Thread} class; it will loop indefinitely until the {@code
 * shutdown} method is called, reading {@code Message} objects from a stream, and invoking the correct implementation of
 * {@code ExecutableMessage} each time a new message is received.
 *
 * @author Caleb Brinkman
 */
public class Connection
{
    private final PingTracker pingTracker;
    private final ExecutableMessageQueue executableMessageQueue;
    private final MessageFactory messageFactory;
    private final MessageIO messageIO;
    private final Thread messageReaderThread;
    private String name = "Connection";
    private final Map<InetAddress, Key> verifiedKeys = new HashMap<>();

    /**
     * Construct a new {@code Connection} that utilizes the specified {@code MessageIO} to read and write messages.
     *
     * @param streams The {@code MessageIO} containing streams used to read and write messages.
     */
    protected Connection(MessageIO streams) {
        this.messageIO = streams;
        pingTracker = new PingTracker();
        executableMessageQueue = new ExecutableMessageQueue();
        messageFactory = new MessageFactory();
        messageReaderThread = new Thread(new RunnableMessageReader(this));
    }

    /**
     * Start the message reader thread managed by this connection.
     */
    public void start() {
        messageReaderThread.start();
    }

    /**
     * Set the RSA public/private key pair used to encrypt outgoing and decrypt incoming messages, and queue a message
     * containing the public key.
     *
     * @param rsaKeyPair The keypair to use for encryption/decrytion.
     */
    public void setRSAKeyPair(KeyPair rsaKeyPair) {
        if (rsaKeyPair != null)
        {
            getMessageIO().getIn().setPrivateKey(rsaKeyPair.getPrivate());
            Message message = MessageFactory.generatePublicKeyMessage(rsaKeyPair.getPublic());
            getMessageIO().queueOutgoingMessage(message);
        }
    }

    /**
     * Get the MessageIO containing the keys and streams used by this connection.
     *
     * @return The MessageIO containing the keys and streams used by this connection.
     */
    public MessageIO getMessageIO() { return messageIO; }

    /**
     * Get the {@code MessageFactory} to be used to generate the {@code Message} objects to be written by this {@code
     * Connection}.
     *
     * @return The {@code MessageFactory} to be used to generate the {@code Message} objects to be written by this
     * {@code Connection}.
     */
    // TODO This should really be a static class with Connections passed as parameters.
    public MessageFactory getMessageFactory() { return messageFactory; }

    /**
     * Get the PingTracker used by this connection to track latency.
     *
     * @return The PingTracker used by this connection to track latency.
     */
    public PingTracker getPingTracker() { return pingTracker; }

    /**
     * Get the {@code ExecutableMessageQueue} maintained by this connection.
     *
     * @return The {@code ExecutableMessageQueue} maintained by this connection.
     */
    public ExecutableMessageQueue getExecutableMessageQueue() { return executableMessageQueue; }

    /**
     * End this connection's execution loop and close any streams.
     */
    public void shutdown() {
        messageIO.closeInputStream();
        messageIO.closeOutputStream();
    }

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

    /**
     * Get the map of domains and verified keys for this client.
     *
     * @return The map of domains and verified keys for this client.
     */
    public Map<InetAddress, Key> getVerifiedKeys() {
        return verifiedKeys;
    }
}
