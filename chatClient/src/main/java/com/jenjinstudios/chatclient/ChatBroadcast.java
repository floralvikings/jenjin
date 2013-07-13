package com.jenjinstudios.chatclient;

import com.jenjinstudios.io.BaseMessage;

/**
 * A chat message set fron server to client.
 *
 * @author Caleb Brinkman
 */
public class ChatBroadcast extends BaseMessage
{
	/** The registration ID for this message type. */
	public static final short ID = 5;
	/** The username of the client broadcasting this message. */
	public final String USERNAME;
	/** The message being broadcast. */
	public final String MESSAGE;

	/**
	 * Construct a new ChatBroadcast.
	 *
	 * @param username The username of the client broadcasting the message.
	 * @param message  The message to be broadcast.
	 */
	public ChatBroadcast(String username, String message)
	{
		super(ID, username, message);
		USERNAME = username;
		MESSAGE = message;
	}

	public String toString()
	{
		return USERNAME + ": " + MESSAGE;
	}
}
