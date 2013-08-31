package com.jenjinstudios.jgsf.message;

import com.jenjinstudios.jgsf.WorldClientHandler;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.sql.WorldSQLHandler;

/**
 * Handles requests to log out of the world.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutRequest extends WorldExecutableMessage
{
	/** The SQLHandler used to log out the client. */
	private final WorldSQLHandler sqlHandler;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ExecutableWorldLogoutRequest(WorldClientHandler handler, Message message)
	{
		super(handler, message);
		sqlHandler = handler.getServer().getSqlHandler();

	}

	@Override
	public void runSynced()
	{
	}

	@Override
	public void runASync()
	{
		if (sqlHandler != null && getClientHandler().isLoggedIn())
		{
			WorldClientHandler handler = getClientHandler();

			boolean success = sqlHandler.logOutPlayer(handler.getActor());

			handler.sendLogoutStatus(success);
		}
	}
}
