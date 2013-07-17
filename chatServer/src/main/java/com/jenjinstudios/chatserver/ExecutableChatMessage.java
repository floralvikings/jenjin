package com.jenjinstudios.chatserver;

import com.jenjinstudios.chatclient.ChatClient;
import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.jgsf.ExecutableMessage;
import com.jenjinstudios.jgsf.Server;
import com.jenjinstudios.message.BaseMessage;

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
	private final BaseMessage msg;
	/** The server which will be executing this message. */
	private final Server<ChatClientHandler> server;
	/** The client handler which created this executable message. */
	private final ChatClientHandler handler;

	/**
	 * Create a chat message with the given string.
	 *
	 * @param handler The client handler which created this executable message.
	 * @param msg     The message to be sent.
	 */
	public ExecutableChatMessage(ChatClientHandler handler, BaseMessage msg)
	{
		super(handler, msg);
		this.msg = msg;
		this.server = handler.getServer();
		this.handler = handler;
	}

	@Override
	public void runSynced()
	{
		ArrayList<ChatClientHandler> clientHandlers = server.getClientHandlers();
		for (ClientHandler h : clientHandlers)
		{
			if (h == null)
				continue;
			ChatClientHandler currentHandler = (ChatClientHandler) h;
			String message = (String) msg.getArgs()[0];
			BaseMessage response = new BaseMessage(ChatClient.CHAT_BROADCAST_ID, handler.getUsername(), message);
			currentHandler.queueMessage(response);
		}
	}

	@Override
	public void runASync()
	{
	}

	@Override
	public short getBaseMessageID()
	{
		return ChatClient.CHAT_MESSAGE_ID;
	}

}
