package com.jenjinstudios.world.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldClientHandler;
import com.jenjinstudios.world.sql.WorldSQLConnector;

/**
 * Handles requests to log out of the world.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutRequest extends WorldExecutableMessage
{
	/** The SQLHandler used to log out the client. */
	private final WorldSQLConnector sqlHandler;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableWorldLogoutRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
		sqlHandler = handler.getServer().getSqlConnector();

	}

	@Override
	public void runSynced() {
		Actor clientActor = getClientHandler().getPlayer();
		// Multiple logout requests can cause Player to be null; have to check first.
		if (clientActor != null)
		{
			clientActor.getWorld().removeObject(clientActor);
		}
	}

	@Override
	public void runASync() {
		WorldClientHandler handler = getClientHandler();
		if (sqlHandler != null && handler.getUser() != null)
		{
			boolean success = sqlHandler.logOutPlayer(handler.getPlayer());

			handler.sendLogoutStatus(success);
			if (success)
			{
				handler.setUser(null);
			}
		}
	}
}
