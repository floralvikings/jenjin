package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.database.DbException;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;

/**
 * Handles requests to login to the world.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequest extends WorldExecutableMessage
{
	private final Authenticator<Player> authenticator;
	private Message loginResponse;

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
		if (getClientHandler().getUser() != null)
		{
			handleLoginSuccess();
			getClientHandler().getUser().addPreUpdateEvent(Vision.EVENT_NAME, new Vision(getClientHandler().getUser()));
			((WorldServer) getClientHandler().getServer()).getWorld().getWorldObjects().add(
				  getClientHandler().getUser());
			loginResponse.setArgument("id", getClientHandler().getUser().getId());
		}
		getClientHandler().getMessageIO().queueOutgoingMessage(loginResponse);
	}

	@Override
	public void runImmediate() {
		try
		{
			tryLogInUser();
		} catch (DbException e)
		{
			handleLoginFailure();
		}
		getClientHandler().setLoggedInTime(getClientHandler().getServer().getCycleStartTime());
	}

	private void tryLogInUser() throws DbException {
		WorldClientHandler handler = getClientHandler();
		if ((authenticator != null) && (handler.getUser() == null))
		{
			String username = (String) getMessage().getArgument("username");
			String password = (String) getMessage().getArgument("password");
			Player player = (Player) authenticator.logInUser(username, password);
			handler.setUser(player);
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
		loginResponse = createSuccessResponse(getClientHandler().getUser());
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
