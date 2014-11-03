package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.sql.LoginException;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.sql.WorldAuthenticator;

import java.util.Map;

/**
 * Handles requests to login to the world.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequest extends WorldExecutableMessage
{
	private static final String X_COORD = "xCoord";
	private static final String Y_COORD = "yCoord";
	private static final String ZONE_ID = "zoneID";
	private final WorldAuthenticator authenticator;
	private Message loginResponse;
	private Map<String, Object> playerData;
	private User user;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableWorldLoginRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
		authenticator = ((WorldServer) handler.getServer()).getAuthenticator();
	}

	@Override
	public void runDelayed() {
		if (user != null)
		{
			handleLoginSuccess();
			((WorldServer) getClientHandler().getServer()).getWorld().getWorldObjects().add(
				  getClientHandler().getPlayer());
			loginResponse.setArgument("id", getClientHandler().getPlayer().getId());
		}
		getClientHandler().getMessageIO().queueOutgoingMessage(loginResponse);
	}

	@Override
	public void runImmediate() {
		try
		{
			tryLogInUser();
		} catch (LoginException e)
		{
			handleLoginFailure();
		}
		getClientHandler().setLoggedInTime(getClientHandler().getServer().getCycleStartTime());
	}

	private void tryLogInUser() throws LoginException {
		WorldClientHandler handler = getClientHandler();
		if (authenticator != null && handler.getUser() == null)
		{
			String username = (String) getMessage().getArgument("username");
			String password = (String) getMessage().getArgument("password");
			user = authenticator.logInUser(username, password);
			playerData = authenticator.lookUpUserProperties(username);
		}
	}

	private void handleLoginFailure() {
		this.loginResponse = createFailureResponse();
	}

	private Message createFailureResponse() {
		WorldClientHandler handler = getClientHandler();
		Message loginResponse = handler.getMessageFactory().generateWorldLoginResponse();
		loginResponse.setArgument("success", false);
		loginResponse.setArgument("id", -1);
		loginResponse.setArgument("loginTime", handler.getLoggedInTime());
		loginResponse.setArgument("xCoordinate", 0d);
		loginResponse.setArgument("yCoordinate", 0d);
		loginResponse.setArgument("zoneNumber", -1);
		return loginResponse;
	}

	private void handleLoginSuccess() {
		Actor player = setHandlerPlayerInfo();
		loginResponse = createSuccessResponse(player);
	}

	private Actor setHandlerPlayerInfo() {
		WorldClientHandler handler = getClientHandler();
		Actor player = handler.getPlayer();
		double x = Double.parseDouble((String) playerData.remove(X_COORD));
		double y = Double.parseDouble((String) playerData.remove(Y_COORD));
		int zoneId = Integer.parseInt((String) playerData.remove(ZONE_ID));
		String username = user.getUsername();
		Vector2D coordinates = new Vector2D(x, y);
		player.setName(username);
		player.setVector2D(coordinates);
		player.setZoneID(zoneId);
		for (String s : playerData.keySet())
		{
			player.getProperties().put(s, playerData.get(s));
		}
		handler.setUser(user);
		return player;
	}

	private Message createSuccessResponse(Actor player) {
		Message loginResponse = getClientHandler().getMessageFactory().generateWorldLoginResponse();
		loginResponse.setArgument("success", true);
		loginResponse.setArgument("loginTime", getClientHandler().getLoggedInTime());
		loginResponse.setArgument("xCoordinate", player.getVector2D().getXCoordinate());
		loginResponse.setArgument("yCoordinate", player.getVector2D().getYCoordinate());
		loginResponse.setArgument("zoneNumber", player.getZoneID());
		return loginResponse;
	}
}
