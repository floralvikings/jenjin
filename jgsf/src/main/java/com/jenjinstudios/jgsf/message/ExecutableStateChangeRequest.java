package com.jenjinstudios.jgsf.message;

import com.jenjinstudios.jgsf.WorldClientHandler;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.World;

/**
 * Process a StateChangeRequest.
 *
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeRequest extends WorldExecutableMessage
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
		World world = getClientHandler().getServer().getWorld();
	}

	@Override
	public void runASync()
	{
	}
}
