package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.net.ClientHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executable message to handle client logging out.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutRequest extends ServerExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableLogoutRequest.class.getName());
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
	public void execute() {
		ClientHandler handler = getClientHandler();
		User user = handler.getUser();
		if ((authenticator != null) && (user != null))
		{
			String username = user.getUsername();
			try
			{
				user = authenticator.logOutUser(username);
				handler.sendLogoutStatus(!user.isLoggedIn());
				handler.setUser(null);
			} catch (AuthenticationException e)
			{
				LOGGER.log(Level.INFO, "Exception when logging out user " + user.getUsername(), e);
				handler.sendLogoutStatus(false);
			}
		}
	}
}
