package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.net.ServerMessageContext;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;

/**
 * Handles requests to login to the world.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequest extends WorldExecutableMessage<ServerMessageContext>
{
	private final Authenticator<Player> authenticator;
	private Message loginResponse;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldLoginRequest(WorldClientHandler handler, Message message, ServerMessageContext context) {
		super(handler, message, context);
		authenticator = ((WorldServer) handler.getServer()).getAuthenticator();
	}

	@Override
	public Message execute() {
		try
		{
			tryLogInUser();
		} catch (AuthenticationException e)
		{
			handleLoginFailure();
		}
		long result = getClientHandler().getServer().getServerUpdateTask().getCycleStartTime();
		getContext().setLoggedInTime(result);
		World world = ((WorldServer) getClientHandler().getServer()).getWorld();
		world.scheduleUpdateTask(() -> {
			if (getClientHandler().getUser() != null)
			{
				handleLoginSuccess();
				getClientHandler().getUser().addPreUpdateEvent(Vision.EVENT_NAME, new Vision(getClientHandler()
					  .getUser()));
				world.getWorldObjects().add(
					  getClientHandler().getUser());
				loginResponse.setArgument("id", getClientHandler().getUser().getId());
			}
		});
		return loginResponse;
	}

	private void tryLogInUser() throws AuthenticationException {
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
		Message loginResponse = WorldServerMessageFactory.generateWorldLoginResponse();
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
		Message loginResponse = WorldServerMessageFactory.generateWorldLoginResponse();
		loginResponse.setArgument("success", true);
		loginResponse.setArgument("loginTime", getClientHandler().getLoggedInTime());
		loginResponse.setArgument("xCoordinate", player.getVector2D().getXCoordinate());
		loginResponse.setArgument("yCoordinate", player.getVector2D().getYCoordinate());
		loginResponse.setArgument("zoneNumber", player.getZoneID());
		return loginResponse;
	}
}
