package com.jenjinstudios.chatclient;

import com.jenjinstudios.jgcf.Client;

import java.io.IOException;
import java.util.LinkedList;

/** @author Caleb Brinkman */
public class ChatClient extends Client
{
	/** The list of received chat messages. */
	private final LinkedList<ChatBroadcast> chatMessages;

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
	public final void sendChatMessage(ChatMessage message)
	{
		queueMessage(message);
	}

	/**
	 * Get all the chat messages collected since the last check.
	 *
	 * @return A {@code LinkedList} of all the ChatMessages collected since the last time this method was
	 *         called.
	 */
	public final LinkedList<ChatBroadcast> getChatMessages()
	{
		LinkedList<ChatBroadcast> temp;
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
	protected void processChatBroadcast(ChatBroadcast message)
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
	protected void processMessage(Object message) throws IOException
	{
		if (message instanceof ChatBroadcast)
			processChatBroadcast((ChatBroadcast) message);
		else
			super.processMessage(message);
	}
}
