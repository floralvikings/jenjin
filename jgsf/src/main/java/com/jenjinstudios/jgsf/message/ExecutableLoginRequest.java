package com.jenjinstudios.jgsf.message;

import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.sql.SQLHandler;

/**
 * Executes the necessary actions to deal with a login response.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginRequest extends ServerExecutableMessage
{
	/** The SQL handler used by this executable message. */
	private final SQLHandler sqlHandler;

	/**
	 * Construct a new ExecutableLoginRequest.
	 *
	 * @param clientHandler The handler which created this executable message.
	 * @param loginRequest  The request sent by the client.
	 */
	public ExecutableLoginRequest(ClientHandler clientHandler, Message loginRequest)
	{
		super(clientHandler, loginRequest);
		sqlHandler = clientHandler.getServer().getSqlHandler();
	}

	@Override
	public void runSynced()
	{
	}

	@Override
	public void runASync()
	{
		if (sqlHandler == null || getClientHandler().isLoggedIn())
			return;
		String username = (String) getMessage().getArgument("username");
		String password = (String) getMessage().getArgument("password");
		boolean success = sqlHandler.logInUser(username, password);
		getClientHandler().sendLoginStatus(success);
		if (success)
			getClientHandler().setUsername(username);
	}

}
