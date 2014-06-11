package com.jenjinstudios.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.net.Connection;

/**
 * Used to generate messages to be passed between client and server.
 * @author Caleb Brinkman
 */
public class MessageFactory
{
	/**
	 * The connection for which this factory is working.
	 */
	private final Connection connection;
	private final MessageRegistry messageRegistry;

	/**
	 * Construct a new MessageFactory working for the given connection.
	 * @param conn The connection for which this message factory works.
	 * @param messageRegistry The message registery for this factory.
	 */
	public MessageFactory(Connection conn, MessageRegistry messageRegistry) {
		this.connection = conn;
		this.messageRegistry = messageRegistry;
	}

	/**
	 * Generate a "ping" request.
	 * @return A "PintRequest" message.
	 */
	public Message generatePingRequest() {
		Message pingRequest = new Message(getConnection(), "PingRequest");
		pingRequest.setArgument("requestTimeNanos", System.nanoTime());
		return pingRequest;
	}

	/**
	 * Generate an "InvalidMessage" message.
	 * @param message The invalid message that was received.
	 * @return The "InvalidMessage" message.
	 */
	public Message generateInvalidMessage(Message message) {
		Message invalid = new Message(getConnection(), "InvalidMessage");
		invalid.setArgument("messageName", message.name);
		invalid.setArgument("messageID", message.getID());
		return invalid;
	}

	/**
	 * Get the connection for which this MessageFactory works.
	 * @return The connection for which this MessageFactory works.
	 */
	protected Connection getConnection() { return connection; }
}
