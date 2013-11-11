package com.jenjinstudios.message;

import com.jenjinstudios.io.MessageInputStream;
import com.jenjinstudios.jgsf.ClientHandler;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles processing a PublicKeyMessage from the client.
 * @author Caleb Brinkman
 */
public class ExecutablePublicKeyMessage extends ServerExecutableMessage
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutablePublicKeyMessage.class.getName());

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutablePublicKeyMessage(ClientHandler handler, Message message) {
		super(handler, message);
	}

	@Override
	public void runSynced() {
	}

	@Override
	public void runASync() {
		Message aesMessage = new Message("AESKeyMessage");
		byte[] encryptedAESKey = MessageInputStream.NO_KEY;
		try
		{
			// Generate an AES key.
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			byte[] aesKeyBytes = keyGenerator.generateKey().getEncoded();

			// Set the output stream and input stream aes key for the client handler.
			getClientHandler().setAESKey(aesKeyBytes);

			// Get the public key from the message.
			byte[] publicKeyBytes = (byte[]) getMessage().getArgument("key");
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

			// Create a cipher using the public key, and encrypt the AES key.
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedAESKey = cipher.doFinal(aesKeyBytes);


		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create AES key!", e);
		} catch (InvalidKeySpecException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create public key from received bytes!", e);
		} catch (NoSuchPaddingException | BadPaddingException e)
		{
			LOGGER.log(Level.SEVERE, "Incorrect padding specified in RSA encryption?!?", e);
		} catch (InvalidKeyException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to encrypt RSA, invalid key received!", e);
		} catch (IllegalBlockSizeException e)
		{
			LOGGER.log(Level.SEVERE, "Illegal block size?!?", e);
		}

		// Construct the AESKeyMessage
		aesMessage.setArgument("key", encryptedAESKey);

		// Send the AESKeyMessage
		getClientHandler().queueMessage(aesMessage);
	}
}
