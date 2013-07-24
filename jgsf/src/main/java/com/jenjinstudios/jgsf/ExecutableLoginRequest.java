package com.jenjinstudios.jgsf;

import com.jenjinstudios.jgcf.Client;
import com.jenjinstudios.message.BaseMessage;

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
	public ExecutableLoginRequest(ClientHandler clientHandler, BaseMessage loginRequest)
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
		String username = (String) getMessage().getArgs()[0];
		String password = (String) getMessage().getArgs()[1];
		boolean success = sqlHandler.logInUser(username, password);
		getClientHandler().sendLoginStatus(success);
		if (success)
			getClientHandler().setUsername(username);
	}

	@Override
	public short getBaseMessageID()
	{
		return Client.LOGIN_REQ_ID;
	}

}
