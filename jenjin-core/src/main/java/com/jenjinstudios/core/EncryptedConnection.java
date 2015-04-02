package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageStreamPair;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains methods used to generate, set and send RSA Keys over a Connection.
 */
public class EncryptedConnection extends Connection
{
	private static final int KEYSIZE = 512;
	private static final Logger LOGGER = Logger.getLogger(EncryptedConnection.class.getName());

	/**
	 * Construct a new {@code Connection} that utilizes the specified {@code MessageIO} to read and write messages.
	 *
	 * @param streams The {@code MessageIO} containing streams used to read and write messages.
	 */
	public EncryptedConnection(MessageStreamPair streams) {
		super(streams);
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
			getMessageStreamPair().getIn().setPrivateKey(rsaKeyPair.getPrivate());
			Message message = generatePublicKeyMessage(rsaKeyPair.getPublic());
			enqueueMessage(message);
		}
	}

}
