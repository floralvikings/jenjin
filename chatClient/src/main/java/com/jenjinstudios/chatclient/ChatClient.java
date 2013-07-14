package com.jenjinstudios.chatclient;

import com.jenjinstudios.message.BaseMessage;
import com.jenjinstudios.jgcf.Client;

import java.io.IOException;
import java.util.LinkedList;

/**
 * The client for the Chat program tutorial.
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class ChatClient extends Client
{
	/** The list of received chat messages. */
	private final LinkedList<BaseMessage> chatMessages;
	/** The ID number for the ChatBroadcast message. */
	public static final short CHAT_BROADCAST_ID = 200;
	/** The ID number for the ChatMessage message. */
	public static final short CHAT_MESSAGE_ID = 201;

	/**
	 * Construct a new ChatClient.
	 * @param address The address of the server.
	 * @param port The port over which to communicate with the server.
	 * @param username The user's username.
	 * @param password The user's password.
	 */
	public ChatClient(String address, int port, String username, String password)
	{
		super(address, port, username, password);
		chatMessages = new LinkedList<>();
	}

	/**
	 * Send a chat message to the server.
	 *
	 * @param message The message to send to the server.
	 */
	public final void sendChatMessage(BaseMessage message)
	{
		queueMessage(message);
	}

	/**
	 * Get all the chat messages collected since the last check.
	 *
	 * @return A {@code LinkedList} of all the ChatMessages collected since the last time this method was
	 *         called.
	 */
	public final LinkedList<BaseMessage> getChatMessages()
	{
		LinkedList<BaseMessage> temp;
		synchronized (chatMessages)
		{
			temp = new LinkedList<>(chatMessages);
			chatMessages.clear();
		}
		return temp;
	}

	/**
	 * Add a chat message to the incoming chat message queue.
	 *
	 * @param message The chat message that hasbeen received.
	 */
	void processChatBroadcast(BaseMessage message)
	{
		synchronized (chatMessages)
		{
			chatMessages.add(message);
		}
	}

	/**
	 * Process the specified message.  This method should be overridden by any implementing classes, but it does
	 * contain functionality necessary to communicate with a DownloadServer or a ChatServer.
	 *
	 * @param message The message to be processed.
	 * @throws java.io.IOException If there is an IO error.
	 */
	protected void processMessage(BaseMessage message) throws IOException
	{
		if (message.getID() == CHAT_BROADCAST_ID)
			processChatBroadcast(message);
		else
			super.processMessage(message);
	}
}
