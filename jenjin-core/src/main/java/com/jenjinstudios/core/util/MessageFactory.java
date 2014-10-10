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
	 * Generate an InvalidMessage message for the given invalid ID and message name.
	 *
	 * @param id The ID of the invalid message.
	 * @param name The Name of the invalid message.
	 *
	 * @return The generated InvalidMessage object.
	 */
	public Message generateInvalidMessage(short id, String name) {
		Message invalid = messageRegistry.createMessage("InvalidMessage");
		invalid.setArgument("messageName", name);
		invalid.setArgument("messageID", id);
		return invalid;
	}

	/**
	 * Generate a PingResponse with the given time of request.
	 *
	 * @param requestTimeMillis The time at which the request for this response was made.
	 *
	 * @return The generated PingResponse.
	 */
	public Message generatePingResponse(long requestTimeMillis) {
		Message pingResponse = MessageRegistry.getInstance().createMessage("PingResponse");
		pingResponse.setArgument("requestTimeMillis", requestTimeMillis);
		return pingResponse;
	}

	/**
	 * Generate a PublicKeyMessage for the given {@code PublicKey}.
	 *
	 * @param publicKey The {@code PublicKey} for which to generate a {@code Message}.
	 *
	 * @return The generated message.
	 */
	public Message generatePublicKeyMessage(PublicKey publicKey) {
		Message publicKeyMessage = MessageRegistry.getInstance().createMessage("PublicKeyMessage");
		publicKeyMessage.setArgument("publicKey", publicKey.getEncoded());
		return publicKeyMessage;
	}

}
