package com.jenjinstudios.jgsf.world.message;

import com.jenjinstudios.jgsf.world.WorldClientHandler;
import com.jenjinstudios.jgsf.world.actor.Actor;
import com.jenjinstudios.jgsf.world.sql.WorldSQLHandler;
import com.jenjinstudios.message.Message;

import java.util.TreeMap;

/**
 * Handles requests to login to the world.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequest extends WorldExecutableMessage
{
	/** The SQL handler used by this executable message. */
	private final WorldSQLHandler sqlHandler;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ExecutableWorldLoginRequest(WorldClientHandler handler, Message message)
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
		if (sqlHandler == null || getClientHandler().isLoggedIn())
			return;
		String username = (String) getMessage().getArgument("username");
		String password = (String) getMessage().getArgument("password");
		TreeMap<String, Object> playerInfo = sqlHandler.logIntoWorld(username, password);

		boolean success = playerInfo != null;
		getClientHandler().setLoginStatus(success);

		Message loginResponse = new Message("WorldLoginResponse");
		loginResponse.setArgument("success", success);

		if (success)
		{
			Actor player = new Actor(username);
			double xCoord = (double) playerInfo.get(WorldSQLHandler.X_COORD);
			double zCoord = (double) playerInfo.get(WorldSQLHandler.Z_COORD);
			player.setVector2D(xCoord, zCoord);
			getClientHandler().setActor(player);
			loginResponse.setArgument("loginTime", getClientHandler().getLoggedInTime());
			loginResponse.setArgument("xCoord", xCoord);
			loginResponse.setArgument("zCoord", zCoord);
		} else
		{
			loginResponse.setArgument("loginTime", getClientHandler().getLoggedInTime());
			loginResponse.setArgument("xCoord", 0);
			loginResponse.setArgument("zCoord", 0);
		}

		getClientHandler().queueMessage(loginResponse);
	}
}
