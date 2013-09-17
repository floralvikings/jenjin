package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.WorldClient;
import com.jenjinstudios.message.Message;

/**
 * Process an ActorVisibleMessage.
 *
 * @author Caleb Brinkman
 */
public class ExecutableActorVisibleMessage extends WorldClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param client  The client invoking this message.
	 * @param message The Message.
	 */
	protected ExecutableActorVisibleMessage(WorldClient client, Message message)
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
