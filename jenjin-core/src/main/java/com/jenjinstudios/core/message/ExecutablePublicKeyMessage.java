package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to set a {@code PublicKeyMessage} for a {@code MessageOutputStream}.
 *
 * @author Caleb Brinkman
 */
public class ExecutablePublicKeyMessage extends ExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(ExecutablePublicKeyMessage.class.getName());
	private final Connection connection;

	@SuppressWarnings("WeakerAccess")
	public ExecutablePublicKeyMessage(Connection connection, Message message) {
		super(message);
		this.connection = connection;
	}

	@Override
	public void runDelayed() {

	}

	@Override
	public void runImmediate() {
		byte[] keyBytes = (byte[]) getMessage().getArgument("publicKey");
		try
		{
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
			connection.setPublicKey(publicKey);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.INFO, "Unable to instantiate public key; messages will not be encrypted!", e);
		}
	}
}
