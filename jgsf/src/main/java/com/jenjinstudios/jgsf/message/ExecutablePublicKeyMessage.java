package com.jenjinstudios.jgsf.message;

import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.message.Message;

/**
 * This class handles processing a PublicKeyMessage from the client.
 *
 * @author Caleb Brinkman
 */
public class ExecutablePublicKeyMessage extends ServerExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ExecutablePublicKeyMessage(ClientHandler handler, Message message)
	{
		super(handler, message);
	}

	@Override
	public void runSynced()
	{
	}

	@Override
	public void runASync()
	{
	}
}
