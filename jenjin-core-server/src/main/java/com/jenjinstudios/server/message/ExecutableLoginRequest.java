package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.sql.LoginException;
import com.jenjinstudios.server.sql.Authenticator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes the necessary actions to deal with a login response.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginRequest extends ServerExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableLoginRequest.class.getName());
	/** The SQL handler used by this executable message. */
	private final Authenticator authenticator;

	/**
	 * Construct a new ExecutableLoginRequest.
	 * @param clientHandler The handler which created this executable message.
	 * @param loginRequest The request sent by the client.
	 */
	public ExecutableLoginRequest(ClientHandler clientHandler, Message loginRequest) {
		super(clientHandler, loginRequest);
		authenticator = clientHandler.getServer().getAuthenticator();
	}

	@Override
	public void runSynced() {
	}

	@Override
	public void runASync() {
		ClientHandler handler = getClientHandler();
		String username = (String) getMessage().getArgument("username");
		String password = (String) getMessage().getArgument("password");
		try
		{
			User user = authenticator.logInUser(username, password);
			long loggedInTime = handler.getServer().getCycleStartTime();
			handler.setLoggedInTime(loggedInTime);
			queueLoginSuccessResponse(loggedInTime);
			handler.setUser(user);
			handler.getServer().associateUsernameWithClientHandler(username, handler);
		} catch (LoginException | NullPointerException e)
		{
			LOGGER.log(Level.FINEST, "User login failure: ", e);
			queueLoginFailureResponse();
		}
	}

	private void queueLoginSuccessResponse(long loggedInTime) {
		Message loginResponse = getClientHandler().getMessageFactory().generateLoginResponse(true, loggedInTime);
		getClientHandler().queueMessage(loginResponse);
	}

	private void queueLoginFailureResponse() {
		Message loginResponse = getClientHandler().getMessageFactory().generateLoginResponse(false, 0);
		getClientHandler().queueMessage(loginResponse);
	}

}
