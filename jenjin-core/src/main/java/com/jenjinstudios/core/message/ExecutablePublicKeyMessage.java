package com.jenjinstudios.core.message;

import com.jenjinstudios.core.EncryptedConnection;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.concurrency.MessageContext;
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
public class ExecutablePublicKeyMessage extends ExecutableMessage<MessageContext>
{
    private static final Logger LOGGER = Logger.getLogger(ExecutablePublicKeyMessage.class.getName());

    /**
     * Construct a new {@code ExecutablePublicKeyMessage}.
     *
     * @param connection The connection invoking this executable message.
     * @param message The message which caused this executable message to be invoked.
	 * @param context The context in which to execute the message.
	 */
    @SuppressWarnings("WeakerAccess")
	public ExecutablePublicKeyMessage(EncryptedConnection connection, Message message, MessageContext context) {
		super(connection, message, context);
	}

    @Override
	public Message execute() {
		byte[] keyBytes = (byte[]) getMessage().getArgument("publicKey");
        try
        {
            PublicKey suppliedKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            if (isKeyValid(suppliedKey))
            {
				getContext().setEncryptionKey(suppliedKey);
			}
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e)
        {
            LOGGER.log(Level.INFO, "Unable to instantiate public key; messages will not be encrypted!", e);
        }
		return null;
	}

    private boolean isKeyValid(Key suppliedKey) {
        boolean verified = false;
		if (((EncryptedConnection) getThreadPool()).getVerifiedKeys().isEmpty())
		{
            verified = true;
        } else
        {
			InetAddress address = getThreadPool().getMessageStreamPair().getAddress();
			if (address != null)
            {
				Key key = ((EncryptedConnection) getThreadPool()).getVerifiedKeys().get(address);
				if ((key == null) || !key.equals(suppliedKey))
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
        }
        return verified;
    }
}
