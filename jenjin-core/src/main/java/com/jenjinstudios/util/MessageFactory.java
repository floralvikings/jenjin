package com.jenjinstudios.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageRegistry;

/**
 * Used to generate messages to be passed between client and server.
 * @author Caleb Brinkman
 */
public class MessageFactory
{
	private final MessageRegistry messageRegistry;

	/**
	 * Construct a new MessageFactory working for the given connection.
	 * @param messageRegistry The message registry for this factory.
	 */
	public MessageFactory(MessageRegistry messageRegistry) { this.messageRegistry = messageRegistry; }

	/**
	 * Generate a "ping" request.
	 * @return A "PintRequest" message.
	 */
	public Message generatePingRequest() {
		Message pingRequest = messageRegistry.createMessage("PingRequest");
		pingRequest.setArgument("requestTimeNanos", System.nanoTime());
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

	public MessageRegistry getMessageRegistry() { return messageRegistry; }

}
