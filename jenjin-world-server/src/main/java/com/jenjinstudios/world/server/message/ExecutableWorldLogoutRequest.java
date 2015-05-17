package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.message.ServerMessageFactory;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldServerMessageContext;

/**
 * Handles requests to log out of the world.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutRequest extends WorldExecutableMessage<WorldServerMessageContext<Player>>
{
	/** The SQLHandler used to log out the client. */
	private final Authenticator<Player> authenticator;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param message The message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldLogoutRequest(Message message, WorldServerMessageContext<Player> context)
	{
		super(message, context);
		authenticator = getContext().getAuthenticator();

	}

	@Override
	public Message execute() {
		Message response;
		try
		{
			tryLogOutUser();
			response = ServerMessageFactory.generateLogoutResponse(true);
		} catch (AuthenticationException e)
		{
			response = ServerMessageFactory.generateLogoutResponse(false);
		}

		Player clientActor = getContext().getUser();
		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			if (clientActor != null)
			{
				getContext().
					  getWorld().
					  getWorldObjects().
					  remove(clientActor.getId());
			}
		});
		return response;
	}

	private void tryLogOutUser() throws AuthenticationException {
		if ((authenticator != null) && (getContext().getUser() != null))
		{
			authenticator.logOutUser(getContext().getUser());
		} else throw new AuthenticationException("Missing ClientHandler username or Authenticator.");
	}
}
