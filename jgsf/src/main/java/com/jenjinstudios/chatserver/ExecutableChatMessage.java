package com.jenjinstudios.chatserver;

import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.jgsf.ExecutableMessage;
import com.jenjinstudios.message.ChatBroadcast;
import com.jenjinstudios.message.ChatMessage;

import java.util.ArrayList;

/**
 * This class is used to send chat messages.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableChatMessage extends ExecutableMessage
{
	/** The String containing the message to send. */
	private final ChatMessage msg;
	/** The server which will be executing this message. */
	private final ChatServer server;
	/** The client handler which created this executable message. */
	private final ChatClientHandler handler;
	/** Whether or not the client has permission to send this message. */
	private boolean permission = false;

	/**
	 * Create a chat message with the given string.
	 *
	 * @param handler The client handler which created this executable message.
	 * @param msg     The message to be sent.
	 */
	public ExecutableChatMessage(ChatClientHandler handler, ChatMessage msg)
	{
		super(handler, msg);
		this.msg = msg;
		this.server = (ChatServer) handler.getServer();
		this.handler = handler;
	}

	@Override
	public void runSynced()
	{
		if (!permission)
			return;

		ArrayList<ClientHandler> clientHandlers = server.getClientHandlers();
		for (ClientHandler h : clientHandlers)
		{
			if (h == null)
				continue;
			ChatClientHandler currentHandler = (ChatClientHandler) h;
			if (currentHandler.inChatGroup(msg.GROUP_ID))
				currentHandler.queueMessage(new ChatBroadcast(handler.getUsername(), msg.MESSAGE));
		}
	}

	@Override
	public void runASync()
	{
		permission = handler.hasChatPermission(msg.GROUP_ID) && handler.isLoggedIn();
	}

}
