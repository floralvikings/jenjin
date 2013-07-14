package com.jenjinstudios.chatserver;

import com.jenjinstudios.chatclient.ChatClient;
import com.jenjinstudios.jgsf.ExecutableMessage;
import com.jenjinstudios.message.BaseMessage;

/**
 * Processes a ChatUsernameMessage.  This class is invoked with reflection and should not be directly referenced.
 *
 * @author Caleb Brinkman
 */
public class ExecutableChatUsername extends ExecutableMessage
{
	/** The client handler which created this executable message. */
	private final ChatClientHandler handler;
	/** The client username. */
	private final String username;

	/**
	 * Used by reflection. Construct a new ExecutableChatUserName.
	 *
	 * @param handler The ChatClientHandler.
	 * @param message The BaseMessage
	 */
	public ExecutableChatUsername(ChatClientHandler handler, BaseMessage message)
	{
		super(handler, message);
		this.handler = handler;
		username = (String) message.getArgs()[0];
	}

	@Override
	public void runSynced()
	{
	}

	@Override
	public void runASync()
	{
		handler.setUsername(username);
	}

	@Override
	public short getBaseMessageID()
	{
		return ChatClient.CHAT_USERNAME_ID;
	}
}
