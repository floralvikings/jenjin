package com.jenjinstudios.core.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

import java.security.Key;

/**
 * This class is used to create {@code Message} objects; these are merely convenience methods.
 *
 * @author Caleb Brinkman
 */
public class MessageFactory
{
    /**
     * Generate a PingRequest message.
     *
     * @return The generated message.
     */
    public static Message generatePingRequest() {
        Message pingRequest = MessageRegistry.getInstance().createMessage("PingRequest");
        pingRequest.setArgument("requestTimeMillis", System.currentTimeMillis());
        return pingRequest;
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

}
