package com.jenjinstudios.core;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.concurrency.MessageThreadPool;
import com.jenjinstudios.core.connection.ConnectionConfig;
import com.jenjinstudios.core.connection.ConnectionInstantiationException;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
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
	 * Construct a new Connection with the given configuration, input stream, and output stream.
	 * @param config The connection configuration.
	 * @param in The input stream.
	 * @param out The output stream.
	 * @throws ConnectionInstantiationException If there is an exception when instantiating the connection.
	 */
	public Connection(ConnectionConfig<T> config, MessageInputStream in, MessageOutputStream out)
		  throws ConnectionInstantiationException
	{
		InetAddress address = config.getAddress();
		Class<T> contextClass = config.getContextClass();

		T context;
		try {
			context = contextClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException
			  e) {
			throw new ConnectionInstantiationException(e);
		}

		for (String s : config.getMessageRegistryFiles()) {
			registerMessages(s);
		}

		messageThreadPool = new MessageThreadPool(in, out, context);
		messageThreadPool.getMessageContext().setAddress(address);
	}

	/**
	 * Construct a new Connection with the given configuration.
	 * @param config The configuration of this connection.
	 * @throws ConnectionInstantiationException If there is an exception when instantiating the connection.
	 */
	public Connection(ConnectionConfig<T> config) throws ConnectionInstantiationException {
		InetAddress address = config.getAddress();
		Class<T> contextClass = config.getContextClass();

		T context;
		try {
			context = contextClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException
			  e) {
			throw new ConnectionInstantiationException(e);
		}

		for (String s : config.getMessageRegistryFiles()) {
			registerMessages(s);
		}

		MessageInputStream inputStream;
		MessageOutputStream outputStream;
		try {
			Socket socket = new Socket(config.getAddress(), config.getPort());
			inputStream = new MessageInputStream(socket.getInputStream());
			outputStream = new MessageOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			throw new ConnectionInstantiationException(e);
		}
		messageThreadPool = new MessageThreadPool(inputStream, outputStream, context);
		messageThreadPool.getMessageContext().setAddress(address);
	}

	/**
	 * Return whether the threads managed by this pool are running.
	 *
	 * @return Whether the threads managed by this pool are running.
	 */
	public boolean isRunning() { return messageThreadPool.isRunning(); }

	private void registerMessages(String s) throws ConnectionInstantiationException {
		File file = new File(s);
		InputStream stream;
		if (file.exists()) {
			try {
				stream = new FileInputStream(s);
			} catch (FileNotFoundException e) {
				throw new ConnectionInstantiationException(e);
			}
		} else {
			stream = getClass().getClassLoader().getResourceAsStream(s);
			if (stream == null) {
				throw new ConnectionInstantiationException("Unable to find message registry " + s);
			}
		}
		MessageRegistry.getGlobalRegistry().register(s, stream);
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
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(KEYSIZE);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
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
		if (rsaKeyPair != null) {
			messageThreadPool.getInputStream().setPrivateKey(rsaKeyPair.getPrivate());
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
