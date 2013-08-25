package com.jenjinstudios.jgcf.world.message;

import com.jenjinstudios.jgcf.Client;
import com.jenjinstudios.jgcf.message.ClientExecutableMessage;
import com.jenjinstudios.message.Message;

/**
 * Process a StateChangeMessage.
 *
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeMessage extends ClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param client  The client invoking this message.
	 * @param message The Message.
	 */
	protected ExecutableStateChangeMessage(Client client, Message message)
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
