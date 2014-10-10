package com.jenjinstudios.core.util;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

import java.security.PublicKey;

/**
 * This class is used to create {@code Message} objects; these are merely convenience methods.
 *
 * @author Caleb Brinkman
 */
// TODO Make all of these methods static.
public class MessageFactory
{
	// TODO There's no reason to maintain an instance of this.
	private final MessageRegistry messageRegistry;

	/**
	 * Construct a new {@code MessageFactory}.
	 */
	public MessageFactory() { this.messageRegistry = MessageRegistry.getInstance(); }

	public Message generatePingRequest() {
		Message pingRequest = MessageRegistry.getInstance().createMessage("PingRequest");
		pingRequest.setArgument("requestTimeMillis", System.currentTimeMillis());
		return pingRequest;
	}

	public Message generateInvalidMessage(short id, String name) {
		Message invalid = messageRegistry.createMessage("InvalidMessage");
		invalid.setArgument("messageName", name);
		invalid.setArgument("messageID", id);
		return invalid;
	}

	public Message generatePingResponse(long requestTimeMillis) {
		Message pingResponse = getMessageRegistry().createMessage("PingResponse");
		pingResponse.setArgument("requestTimeMillis", requestTimeMillis);
		return pingResponse;
	}

	public Message generatePublicKeyMessage(PublicKey publicKey) {
		Message publicKeyMessage = getMessageRegistry().createMessage("PublicKeyMessage");
		publicKeyMessage.setArgument("publicKey", publicKey.getEncoded());
		return publicKeyMessage;
	}

	protected MessageRegistry getMessageRegistry() { return messageRegistry; }

}
