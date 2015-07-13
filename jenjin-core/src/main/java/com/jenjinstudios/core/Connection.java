package com.jenjinstudios.core;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.concurrency.MessageThreadPool;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageStreamPair;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Connection class utilizes a MessageInputStream and MessageOutputStream to read and write messages to another
 * connection.  This is done with four seperate Threads, started using the start() method; one for reading messages, one
 * for executing the retrieved messages' ExecutableMessage equivalent, one for writing messages, and one for monitoring
 * the others for errors.
 *
 * @author Caleb Brinkman
 */
public class Connection<T extends MessageContext>
{
	private static final int KEYSIZE = 512;
	private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
	private final MessageThreadPool<T> messageThreadPool;

	/**
	 * Construct a new connection using the given MessageIO for reading and writing messages.
	 *
	 * @param streams The MessageIO containing the input and output streams
	 * @param context The context in which messages should be executed.
	 */
	public Connection(MessageStreamPair streams, T context) {
		messageThreadPool = new MessageThreadPool<>(streams, context);
		messageThreadPool.getMessageContext().setAddress(streams.getAddress());
		messageThreadPool.getMessageContext().setName("Connection");
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/core/io/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core XML Registry", stream);
	}

	/**
	 * Generate a PublicKeyMessage for the given {@code PublicKey}.
	 *
	 * @param publicKey The {@code PublicKey} for which to generate a {@code Message}.
	 *
	 * @return The generated message.
	 */
	public static Message generatePublicKeyMessage(Key publicKey) {
		Message publicKeyMessage = MessageRegistry.getGlobalRegistry().createMessage("PublicKeyMessage");
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
	 * Set the RSA public/private key pair used to encrypt outgoing and decrypt incoming messages, and queue a message
	 * containing the public key.
	 *
	 * @param rsaKeyPair The keypair to use for encryption/decrytion.
	 */
	public void setRSAKeyPair(KeyPair rsaKeyPair) {
		if (rsaKeyPair != null)
		{
			messageThreadPool.getMessageStreamPair().getIn().setPrivateKey(rsaKeyPair.getPrivate());
			Message message = generatePublicKeyMessage(rsaKeyPair.getPublic());
			messageThreadPool.enqueueMessage(message);
		}
	}

	/**
	 * Shutdown this connection.
	 */
	public void shutdown() { messageThreadPool.shutdown(); }

	/**
	 * Start this connection.
	 */
	public void start() { messageThreadPool.start(); }

	/**
	 * Get the message context of this Connection.
	 *
	 * @return The message context of this Connection.
	 */
	public T getMessageContext() { return messageThreadPool.getMessageContext(); }

	/**
	 * Queue up the supplied message to be written.
	 *
	 * @param message The message to be sent.
	 */
	public void enqueueMessage(Message message) { messageThreadPool.enqueueMessage(message); }

	/**
	 * Get the unique ID of this MessageThreadPool.
	 *
	 * @return The unique ID of this MessageThreadPool.
	 */
	public String getId() { return messageThreadPool.getId(); }
}
