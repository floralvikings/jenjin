package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.Client;
import com.jenjinstudios.message.Message;

/**
 * Handles processing an AESKeyMessage from the server.
 *
 * @author Caleb Brinkman
 */
public class ExecutableAESKeyMessage extends ClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param client  The client invoking this message.
	 * @param message The Message.
	 */
	protected ExecutableAESKeyMessage(Client client, Message message)
	{
		super(client, message);
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
