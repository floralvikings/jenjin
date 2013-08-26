package com.jenjinstudios.jgsf.world.message;

import com.jenjinstudios.jgsf.world.WorldClientHandler;
import com.jenjinstudios.message.Message;

/**
 * Handles requests to login to the world.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequest extends WorldExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ExecutableWorldLoginRequest(WorldClientHandler handler, Message message)
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
