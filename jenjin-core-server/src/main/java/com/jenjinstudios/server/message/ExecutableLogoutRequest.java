package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.sql.LoginException;
import com.jenjinstudios.server.sql.Authenticator;

/**
 * Executable message to handle client logging out.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutRequest extends ServerExecutableMessage
{
	/** The SQLHandler used to log out the client. */
	private final Authenticator authenticator;

	/**
	 * Construct a new ExecutableLogoutRequest.
	 * @param clientHandler The client handler which created this message.
	 * @param message The message used to create this ExecutableMessage.
	 */
	public ExecutableLogoutRequest(ClientHandler clientHandler, Message message) {
		super(clientHandler, message);
		authenticator = clientHandler.getServer().getAuthenticator();
	}

	@Override
	public void runDelayed() {
	}

	@Override
	public void runImmediate() {
		ClientHandler handler = getClientHandler();
		User user = handler.getUser();
		if (authenticator == null || user == null)
			return;
		String username = user.getUsername();
		try
		{
			authenticator.logOutUser(username);
			handler.sendLogoutStatus(true);
			handler.getServer().associateUsernameWithClientHandler(username, null);
			handler.setUser(null);
		} catch (LoginException e)
		{
			handler.sendLogoutStatus(false);
		}
	}

}
