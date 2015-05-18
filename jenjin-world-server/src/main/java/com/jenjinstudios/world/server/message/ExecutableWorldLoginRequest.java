package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldServerMessageContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles requests to login to the world.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequest extends WorldExecutableMessage<WorldServerMessageContext<Player>>
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableWorldLoginRequest.class.getName());
	private final Authenticator<Player> authenticator;
	private Message loginResponse;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *  @param message The message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldLoginRequest(Message message, WorldServerMessageContext<Player> context) {
		super(message, context);
		authenticator = getContext().getAuthenticator();
	}

	@Override
	public Message execute() {
		try
		{
			tryLogInUser();
		} catch (AuthenticationException e)
		{
			LOGGER.log(Level.INFO, "Encountered exception when authenticating user", e);
			handleLoginFailure();
		}
		long result = System.currentTimeMillis();
		getContext().setLoggedInTime(result);
		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			if (getContext().getUser() != null)
			{
				handleLoginSuccess();
				world.getWorldObjects().add(getContext().getUser());
				loginResponse.setArgument("id",
					  getContext().getUser().getIdentification().getId());
				getContext().enqueue(loginResponse);
			}
		});
		return null;
	}

	private void tryLogInUser() throws AuthenticationException {
		if ((authenticator != null) && (getContext().getUser() == null))
		{
			String username = (String) getMessage().getArgument("username");
			String password = (String) getMessage().getArgument("password");
			Player player = authenticator.logInUser(username, password);
			getContext().setUser(player);
		}
	}

	private void handleLoginFailure() {
		this.loginResponse = createFailureResponse();
	}

	private Message createFailureResponse() {
		Message loginResponse = WorldServerMessageFactory.generateWorldLoginResponse();
		loginResponse.setArgument("success", false);
		loginResponse.setArgument("id", -1);
		loginResponse.setArgument("loginTime", getContext().getLoggedInTime());
		loginResponse.setArgument("xCoordinate", 0d);
		loginResponse.setArgument("yCoordinate", 0d);
		loginResponse.setArgument("zoneNumber", -1);
		return loginResponse;
	}

	private void handleLoginSuccess() { loginResponse = createSuccessResponse(getContext().getUser()); }

	private Message createSuccessResponse(Actor player) {
		Message loginResponse = WorldServerMessageFactory.generateWorldLoginResponse();
		loginResponse.setArgument("success", true);
		loginResponse.setArgument("loginTime", getContext().getLoggedInTime());
		loginResponse.setArgument("xCoordinate", player.getGeometry2D().getPosition().getXValue());
		loginResponse.setArgument("yCoordinate", player.getGeometry2D().getPosition().getYValue());
		loginResponse.setArgument("zoneNumber", player.getZoneID());
		return loginResponse;
	}
}
