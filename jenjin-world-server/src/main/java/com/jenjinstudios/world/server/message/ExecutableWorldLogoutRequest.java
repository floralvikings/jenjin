package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.database.DbException;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;

/**
 * Handles requests to log out of the world.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutRequest extends WorldExecutableMessage
{
	/** The SQLHandler used to log out the client. */
	private final Authenticator<Player> authenticator;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableWorldLogoutRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
		authenticator = ((WorldServer) handler.getServer()).getAuthenticator();

	}

	@Override
	public void runDelayed() {
		Player clientActor = getClientHandler().getUser();
		// Multiple logout requests can cause Player to be null; have to check first.
		if ((clientActor != null) && !clientActor.isLoggedIn())
		{
			clientActor.getWorld().getWorldObjects().remove(clientActor.getId());
		}
	}

	@Override
	public void runImmediate() {
		try
		{
			tryLogOutUser();
			getClientHandler().sendLogoutStatus(true);
		} catch (DbException e)
		{
			getClientHandler().sendLogoutStatus(false);
		}

	}

	private void tryLogOutUser() throws DbException {
		WorldClientHandler handler = getClientHandler();
		if (authenticator != null && handler.getUser() != null)
		{
			authenticator.logOutUser(handler.getUser().getUsername());
		} else throw new AuthenticationException("Missing ClientHandler username or Authenticator.");
	}
}
