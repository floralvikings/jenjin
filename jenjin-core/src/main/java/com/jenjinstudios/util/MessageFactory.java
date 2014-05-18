package com.jenjinstudios.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.Connection;

/** @author Caleb Brinkman */
public class MessageFactory
{
	private final Connection connection;

	public MessageFactory(Connection conn) { this.connection = conn; }

	public Message generatePingRequest() {
		Message pingRequest = new Message(getConnection(), "PingRequest");
		pingRequest.setArgument("requestTimeNanos", System.nanoTime());
		return pingRequest;
	}

	public Message generateInvalidMessage(Message message) {
		Message invalid = new Message(getConnection(), "InvalidMessage");
		invalid.setArgument("messageName", message.name);
		invalid.setArgument("messageID", message.getID());
		return invalid;
	}

	protected Connection getConnection() { return connection; }
}
