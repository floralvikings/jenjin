package com.jenjinstudios.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.ClientHandler;
import com.jenjinstudios.sql.SQLHandler;

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
		boolean success = false;
		if (sqlHandler == null || getClientHandler().isLoggedIn()) {
			long loggedInTime = getClientHandler().getLoggedInTime();
			Message loginResponse = getClientHandler().getMessageFactory().generateLoginResponse(getClientHandler(), success, loggedInTime);
			getClientHandler().queueMessage(loginResponse);
			return;
		}
		String username = (String) getMessage().getArgument("username");
		String password = (String) getMessage().getArgument("password");
		success = sqlHandler.logInUser(username, password);

		getClientHandler().setLoginStatus(success);
		long loggedInTime = getClientHandler().getLoggedInTime();
		Message loginResponse = getClientHandler().getMessageFactory().generateLoginResponse(getClientHandler(), success, loggedInTime);
		getClientHandler().queueMessage(loginResponse);

		if (success)
			getClientHandler().setUsername(username);
	}

}
