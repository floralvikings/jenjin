package com.jenjinstudios.jgsf.world.message;

import com.jenjinstudios.jgsf.message.ServerExecutableMessage;
import com.jenjinstudios.jgsf.world.WorldClientHandler;
import com.jenjinstudios.message.Message;

/**
 * Process a StateChangeRequest.
 *
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeRequest extends ServerExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ExecutableStateChangeRequest(WorldClientHandler handler, Message message)
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
