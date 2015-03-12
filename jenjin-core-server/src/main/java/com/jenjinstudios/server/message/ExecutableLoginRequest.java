package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.database.Authenticator;
import com.jenjinstudios.server.database.DbException;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.User;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes the necessary actions to deal with a login response.
 *
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
	 *
	 * @param clientHandler The handler which created this executable message.
	 * @param loginRequest The request sent by the client.
	 */
	public ExecutableLoginRequest(ClientHandler clientHandler, Message loginRequest) {
		super(clientHandler, loginRequest);
		authenticator = clientHandler.getServer().getAuthenticator();
	}

	@Override
	public void runDelayed() {
	}

	@Override
	public void runImmediate() {
		ClientHandler handler = getClientHandler();
		String username = (String) getMessage().getArgument("username");
		String password = (String) getMessage().getArgument("password");
		try
		{
			User user = authenticator.logInUser(username, password);
			if (user != null)
			{
				long loggedInTime = handler.getServer().getCycleStartTime();
				handler.setLoggedInTime(loggedInTime);
				queueLoginSuccessResponse(loggedInTime);
				handler.setUser(user);
			} else
			{
				queueLoginFailureResponse();
			}
		} catch (DbException e)
		{
			LOGGER.log(Level.FINEST, "User login failure: ", e);
			queueLoginFailureResponse();
		}
	}

	private void queueLoginSuccessResponse(long loggedInTime) {
		Message loginResponse = getClientHandler().getMessageFactory().generateLoginResponse(true, loggedInTime);
		getClientHandler().getMessageIO().queueOutgoingMessage(loginResponse);
	}

	private void queueLoginFailureResponse() {
		ClientHandler clientHandler = getClientHandler();
		ServerMessageFactory messageFactory = clientHandler.getMessageFactory();
		Message loginResponse = messageFactory.generateLoginResponse(false, 0);
		clientHandler.getMessageIO().queueOutgoingMessage(loginResponse);
	}

}
