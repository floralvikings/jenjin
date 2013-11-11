package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.io.MessageInputStream;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.net.Client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles processing an AESKeyMessage from the server.
 * @author Caleb Brinkman
 */
public class ExecutableAESKeyMessage extends ClientExecutableMessage
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutableAESKeyMessage.class.getName());

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableAESKeyMessage(Client client, Message message) {
		super(client, message);
	}

	@Override
	public void runSynced() {
	}

	@Override
	public void runASync() {
		byte[] encryptedAESKey = (byte[]) getMessage().getArgument("key");
		if (Arrays.equals(encryptedAESKey, MessageInputStream.NO_KEY))
			return;
		PrivateKey privateKey = getClient().getPrivateKey();
		try
		{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedAESKey = cipher.doFinal(encryptedAESKey);
			getClient().setAESKey(decryptedAESKey);
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to find RSA algorithm!", e);
		} catch (NoSuchPaddingException | BadPaddingException e)
		{
			LOGGER.log(Level.SEVERE, "Incorrect padding specified in RSA encryption?!?", e);
		} catch (InvalidKeyException e)
		{
			LOGGER.log(Level.SEVERE, "Incorrect key!");
		} catch (IllegalBlockSizeException e)
		{
			LOGGER.log(Level.SEVERE, "Illegal block size?!?", e);
		}

	}
}
