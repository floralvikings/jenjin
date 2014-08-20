package com.jenjinstudios.core.util;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

import java.security.PublicKey;

/**
 * Used to generate messages to be passed between client and server.
 * @author Caleb Brinkman
 */
public class MessageFactory
{
	private final MessageRegistry messageRegistry;

	/**
	 * Construct a new MessageFactory working for the given connection.
	 */
	public MessageFactory() { this.messageRegistry = MessageRegistry.getInstance(); }

	/**
	 * Generate a "ping" request.
	 * @return A "PintRequest" message.
	 */
	public Message generatePingRequest() {
		Message pingRequest = MessageRegistry.getInstance().createMessage("PingRequest");
		pingRequest.setArgument("requestTimeMillis", System.currentTimeMillis());
		return pingRequest;
	}

	/**
	 * Generate an "InvalidMessage" message.
	 * @return The "InvalidMessage" message.
	 */
	public Message generateInvalidMessage(short id, String name) {
		Message invalid = messageRegistry.createMessage("InvalidMessage");
		invalid.setArgument("messageName", name);
		invalid.setArgument("messageID", id);
		return invalid;
	}

	/**
	 * Generate a response to a PingRequest.
	 * @param requestTimeMillis The time at which the ping request was made.
	 * @return The PingResponse message.
	 */
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
