package com.jenjinstudios.jgsf.world.message;

import com.jenjinstudios.jgsf.world.WorldClientHandler;
import com.jenjinstudios.message.Message;

/**
 * Handles requests to log out of the world.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutRequest extends WorldExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ExecutableWorldLogoutRequest(WorldClientHandler handler, Message message)
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
