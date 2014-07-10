package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.sql.WorldAuthenticator;

/**
 * Handles requests to login to the world.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequest extends WorldExecutableMessage
{
	/** The SQL handler used by this executable message. */
	private final WorldAuthenticator sqlHandler;
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
		sqlHandler = handler.getServer().getAuthenticator();
	}

	@Override
	public void runDelayed() {
		if (player != null)
		{
			getClientHandler().getServer().getWorld().addObject(player);
			loginResponse.setArgument("id", player.getId());
		}
		getClientHandler().queueOutgoingMessage(loginResponse);

	}

	@Override
	public void runImmediate() {
		boolean success;
		User user = tryLogInUser();

		success = player != null;
		getClientHandler().setLoggedInTime(getClientHandler().getServer().getCycleStartTime());

		initLoginResponse(success);

		if (success)
		{
			prepareSuccessResponse(user);
		} else
		{
			prepareFailureResponse();
		}
	}

	private User tryLogInUser() {
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
		return user;
	}

	private void initLoginResponse(boolean success) {
		WorldClientHandler handler = getClientHandler();
		loginResponse = handler.getMessageFactory().generateWorldLoginResponse();
		loginResponse.setArgument("success", success);
	}

	private void prepareFailureResponse() {
		WorldClientHandler handler = getClientHandler();
		loginResponse.setArgument("id", -1);
		loginResponse.setArgument("loginTime", handler.getLoggedInTime());
		loginResponse.setArgument("xCoordinate", 0d);
		loginResponse.setArgument("yCoordinate", 0d);
		loginResponse.setArgument("zoneNumber", -1);
	}

	private void prepareSuccessResponse(User user) {
		WorldClientHandler handler = getClientHandler();
		handler.setPlayer(player);
		handler.setUser(user);
		handler.getServer().associateUsernameWithClientHandler(user.getUsername(), handler);
		loginResponse.setArgument("loginTime", handler.getLoggedInTime());
		loginResponse.setArgument("xCoordinate", player.getVector2D().getXCoordinate());
		loginResponse.setArgument("yCoordinate", player.getVector2D().getYCoordinate());
		loginResponse.setArgument("zoneNumber", player.getZoneID());
	}
}
