package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.AuthServer;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.sql.SQLHandler;

/**
 * Executes the necessary actions to deal with a login response.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginRequest extends ServerExecutableMessage
{
	/** The SQL handler used by this executable message. */
	private final SQLHandler sqlHandler;

	/**
	 * Construct a new ExecutableLoginRequest.
	 * @param clientHandler The handler which created this executable message.
	 * @param loginRequest The request sent by the client.
	 */
	public ExecutableLoginRequest(ClientHandler clientHandler, Message loginRequest) {
		super(clientHandler, loginRequest);
		sqlHandler = clientHandler.getServer().getSqlHandler();
	}

	@Override
	public void runSynced() {
	}

	@Override
	public void runASync() {
		boolean success;
		ClientHandler handler = getClientHandler();
		AuthServer<? extends ClientHandler> server = handler.getServer();
		Message message = getMessage();
		if (sqlHandler == null || handler.getUser() != null)
		{
			long loggedInTime = handler.getLoggedInTime();
			Message loginResponse = handler.getMessageFactory().generateLoginResponse(false, loggedInTime);
			handler.queueMessage(loginResponse);
			return;
		}
		String username = (String) message.getArgument("username");
		String password = (String) message.getArgument("password");
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		success = sqlHandler.logInUser(user);

		handler.setLoginStatus(success);
		long loggedInTime = handler.getLoggedInTime();
		Message loginResponse = handler.getMessageFactory().generateLoginResponse(success, loggedInTime);
		handler.queueMessage(loginResponse);

		if (success)
			handler.setUser(user);
		server.clientUsernameSet(username, handler);
	}

}
