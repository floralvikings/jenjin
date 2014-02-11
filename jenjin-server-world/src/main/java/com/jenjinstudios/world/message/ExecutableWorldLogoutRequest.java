package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldClientHandler;
import com.jenjinstudios.world.sql.WorldSQLHandler;

/**
 * Handles requests to log out of the world.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutRequest extends WorldExecutableMessage
{
	/** The SQLHandler used to log out the client. */
	private final WorldSQLHandler sqlHandler;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableWorldLogoutRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
		sqlHandler = handler.getServer().getSqlHandler();

	}

	@Override
	public void runSynced() {
		Actor clientActor = getClientHandler().getPlayer();
		clientActor.getWorld().removeObject(clientActor);
	}

	@Override
	public void runASync() {
		if (sqlHandler != null && getClientHandler().isLoggedIn())
		{
			WorldClientHandler handler = getClientHandler();

			boolean success = sqlHandler.logOutPlayer(handler.getPlayer());

			handler.sendLogoutStatus(success);
		}
	}
}
