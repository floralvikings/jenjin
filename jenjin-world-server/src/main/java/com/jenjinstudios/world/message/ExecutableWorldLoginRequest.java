package com.jenjinstudios.world.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.world.Player;
import com.jenjinstudios.world.WorldClientHandler;
import com.jenjinstudios.world.sql.WorldSQLConnector;

/**
 * Handles requests to login to the world.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequest extends WorldExecutableMessage
{
	/** The SQL handler used by this executable message. */
	private final WorldSQLConnector sqlHandler;
	/** The player added to the world. */
	private Player player;
	/** The LoginResponse to send to the client. */
	private Message loginResponse;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableWorldLoginRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
		sqlHandler = handler.getServer().getSqlConnector();
	}

	@Override
	public void runSynced() {
		if (player != null)
		{
			getClientHandler().getServer().getWorld().addObject(player);
			loginResponse.setArgument("id", player.getId());
		}
		getClientHandler().queueMessage(loginResponse);

	}

	@Override
	public void runASync() {
		boolean success;
		User user = new User();
		WorldClientHandler handler = getClientHandler();
		if (sqlHandler != null && handler.getUser() == null)
		{
			String username = (String) getMessage().getArgument("username");
			String password = (String) getMessage().getArgument("password");
			user.setUsername(username);
			user.setPassword(password);
			/* The map used to create the player. */
			player = sqlHandler.logInPlayer(user);
		}

		success = player != null;
		handler.setLoggedInTime(handler.getServer().getCycleStartTime());

		loginResponse = handler.getMessageFactory().generateWorldLoginResponse();
		loginResponse.setArgument("success", success);

		if (success)
		{
			handler.setPlayer(player);
			handler.setUser(user);
			handler.getServer().associateUsernameWithClientHandler(user.getUsername(), handler);
			loginResponse.setArgument("loginTime", handler.getLoggedInTime());
			loginResponse.setArgument("xCoordinate", player.getVector2D().getXCoordinate());
			loginResponse.setArgument("yCoordinate", player.getVector2D().getYCoordinate());
			loginResponse.setArgument("zoneNumber", player.getZoneID());
		} else
		{
			loginResponse.setArgument("id", -1);
			loginResponse.setArgument("loginTime", handler.getLoggedInTime());
			loginResponse.setArgument("xCoordinate", 0d);
			loginResponse.setArgument("yCoordinate", 0d);
			loginResponse.setArgument("zoneNumber", -1);
		}
	}
}
