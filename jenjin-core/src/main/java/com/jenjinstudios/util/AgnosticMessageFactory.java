package com.jenjinstudios.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.Connection;

/** @author Caleb Brinkman */
public class AgnosticMessageFactory
{
	public static Message generatePingRequest(Connection conn) {
		Message pingRequest = new Message(conn, "PingRequest");
		pingRequest.setArgument("requestTimeNanos", System.nanoTime());
		return pingRequest;
	}

	public static Message generateInvalidMessage(Connection connection, Message message) {
		Message invalid = new Message(connection, "InvalidMessage");
		invalid.setArgument("messageName", message.name);
		invalid.setArgument("messageID", message.getID());
		return invalid;
	}
}
