package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.net.ServerMessageContext;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jenjinstudios.server.message.ServerMessageFactory.generateLoginResponse;

/**
 * Executes the necessary actions to deal with a login response.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginRequest extends ServerExecutableMessage<ServerMessageContext<User>>
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableLoginRequest.class.getName());
	/** The SQL handler used by this executable message. */
	private final Authenticator authenticator;

	/**
	 * Construct a new ExecutableLoginRequest.
	 *
	 * @param loginRequest The request sent by the client.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableLoginRequest(Message loginRequest, ServerMessageContext<User> context) {
		super(loginRequest, context);
		authenticator = getContext().getAuthenticator();
	}

	@Override
	public Message execute() {
		String username = (String) getMessage().getArgument("username");
		String password = (String) getMessage().getArgument("password");
		Message response;
		try
		{
			User user = authenticator.logInUser(username, password);
			if (user != null)
			{
				long loggedInTime = System.currentTimeMillis();
				getContext().setLoggedInTime(loggedInTime);
				response = generateLoginResponse(true, loggedInTime);
				getContext().setUser(user);
			} else
			{
				response = generateLoginResponse(false, 0);
			}
		} catch (AuthenticationException e)
		{
			LOGGER.log(Level.FINEST, "User login failure: ", e);
			response = generateLoginResponse(false, 0);
		}
		return response;
	}

}
