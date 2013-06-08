package com.jenjinstudios.message;

import com.jenjinstudios.io.BaseMessage;

/**
 * This class is used to send chat messages.
 *
 * @author Caleb Brinkman
 */
public class ChatMessage extends BaseMessage
{
	/** The registration ID of this message type. */
	public static final short ID = 6;
	/** The deaulf chat group. */
	private static final int DEFAULT_GROUP = 0;
	/** The String containing the message to send. */
	public final String MESSAGE;
	/** The group ID in which to broadcast the message. */
	public final int GROUP_ID;

	/**
	 * Construct a new ChatMessage.
	 *
	 * @param message The message to send.
	 * @param groupID The group ID to send the message to.
	 */
	public ChatMessage(String message, Integer groupID)
	{
		super(message, groupID);
		MESSAGE = message;
		GROUP_ID = groupID;
	}

	/**
	 * Create a chat message with the given string and a default group ID.
	 *
	 * @param message The message to be sent.
	 */
	public ChatMessage(String message)
	{
		this(message, DEFAULT_GROUP);
	}

	@Override
	public String toString()
	{
		return MESSAGE;
	}

	@Override
	public short getID()
	{
		return ID;
	}
}
