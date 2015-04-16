package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.net.ServerMessageContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executable message to handle client logging out.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutRequest extends ServerExecutableMessage<ServerMessageContext<User>>
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableLogoutRequest.class.getName());
	/** The SQLHandler used to log out the client. */
	private final Authenticator authenticator;

	/**
	 * Construct a new ExecutableLogoutRequest.
	 * @param message The message used to create this ExecutableMessage.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableLogoutRequest(Message message, ServerMessageContext<User> context) {
		super(message, context);
		authenticator = getContext().getAuthenticator();
	}

	@Override
	public Message execute() {
		User user = getContext().getUser();
		Message response = null;
		if ((authenticator != null) && (user != null))
		{
			String username = user.getUsername();
			try
			{
				user = authenticator.logOutUser(username);
				response = ServerMessageFactory.generateLogoutResponse(!user.isLoggedIn());
				getContext().setUser(null);
			} catch (AuthenticationException e)
			{
				LOGGER.log(Level.INFO, "Exception when logging out user " + user.getUsername(), e);
				response = ServerMessageFactory.generateLogoutResponse(false);
			}
		}
		return response;
	}
}
