package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

import java.net.InetAddress;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code Connection} class is a subclass of the {@code Thread} class; it will loop indefinitely until the {@code
 * shutdown} method is called, reading {@code Message} objects from a stream, and invoking the correct implementation of
 * {@code ExecutableMessage} each time a new message is received.
 *
 * @author Caleb Brinkman
 */
public class Connection
{
    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
    private static final int KEYSIZE = 512;
    private final PingTracker pingTracker;
    private final ExecutableMessageQueue executableMessageQueue;
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
        messageReaderThread = new Thread(new RunnableMessageReader(this));
    }

    /**
     * Generate a PublicKeyMessage for the given {@code PublicKey}.
     *
     * @param publicKey The {@code PublicKey} for which to generate a {@code Message}.
     *
     * @return The generated message.
     */
    public static Message generatePublicKeyMessage(Key publicKey) {
        Message publicKeyMessage = MessageRegistry.getInstance().createMessage("PublicKeyMessage");
        publicKeyMessage.setArgument("publicKey", publicKey.getEncoded());
        return publicKeyMessage;
    }

    /**
     * Generate an RSA-512 Public-Private Key Pair.
     *
     * @return The generated {@code KeyPair}, or null if the KeyPair could not be created.
     */
    public static KeyPair generateRSAKeyPair() {
        KeyPair keyPair = null;
        try
        {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEYSIZE);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e)
        {
            LOGGER.log(Level.SEVERE, "Unable to create RSA key pair!", e);
        }
        return keyPair;
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
            Message message = generatePublicKeyMessage(rsaKeyPair.getPublic());
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
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Map<InetAddress, Key> getVerifiedKeys() { return verifiedKeys; }
}
