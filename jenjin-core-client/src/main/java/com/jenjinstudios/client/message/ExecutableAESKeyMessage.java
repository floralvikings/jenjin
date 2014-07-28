package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.Client;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles processing an AESKeyMessage from the server.
 * @author Caleb Brinkman
 */
@SuppressWarnings("WeakerAccess")
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
	public void runDelayed() {
	}

	@Override
	public void runImmediate() {
		byte[] encryptedAESKey = (byte[]) getMessage().getArgument("key");
		byte[] decryptedAESKey = MessageInputStream.NO_KEY;
		if (!Arrays.equals(encryptedAESKey, MessageInputStream.NO_KEY))
		{
			PrivateKey privateKey = getClient().getPrivateKey();
			try
			{
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				decryptedAESKey = cipher.doFinal(encryptedAESKey);
			} catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Exception when decrypting key:", e);
			}
			getClient().setAESKey(decryptedAESKey);
		}
	}
}
