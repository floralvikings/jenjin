package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;

import java.net.InetAddress;
import java.security.Key;
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

	/**
	 * Construct a new {@code ExecutablePublicKeyMessage}.
	 *
	 * @param connection The connection invoking this executable message.
	 * @param message The message which caused this executable message to be invoked.
	 */
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
			PublicKey suppliedKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            if (isKeyValid(suppliedKey))
            {
				connection.getMessageIO().setPublicKey(suppliedKey);
			}
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.INFO, "Unable to instantiate public key; messages will not be encrypted!", e);
		}
	}

    private boolean isKeyValid(Key suppliedKey) {
        boolean verified = false;
		if (!connection.getVerifiedKeys().isEmpty())
		{
			InetAddress address = connection.getMessageIO().getAddress();
			if (address != null)
			{
				Key key = connection.getVerifiedKeys().get(address);
				if (key == null || !key.equals(suppliedKey))
				{
					LOGGER.log(Level.SEVERE, "Unable to verify public key; supplied key does not match registry.");
				} else
				{
					verified = true;
				}
			} else
			{
				LOGGER.log(Level.SEVERE, "Unable to verify public key; connection address unknown.");
			}
		} else
		{
			verified = true;
		}
		return verified;
	}
}
