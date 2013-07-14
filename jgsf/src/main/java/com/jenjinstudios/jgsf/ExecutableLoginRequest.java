package com.jenjinstudios.jgsf;

import com.jenjinstudios.jgcf.Client;
import com.jenjinstudios.message.BaseMessage;

/**
 * Executes the necessary actions to deal with a login response.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginRequest extends ExecutableMessage
{
	/** The login request to be handled by this executable message. */
	private final BaseMessage message;
	/** The client handler which created this executable message. */
	private final ClientHandler clientHandler;
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
		this.message = loginRequest;
		this.clientHandler = clientHandler;
		sqlHandler = clientHandler.getServer().getSqlHandler();
	}

	@Override
	public void runSynced()
	{
	}

	@Override
	public void runASync()
	{
		if (sqlHandler == null || clientHandler.isLoggedIn())
			return;
		String username = (String) message.getArgs()[0];
		String password = (String) message.getArgs()[1];
		boolean success = sqlHandler.logInUser(username, password);
		clientHandler.queueLoginStatus(success);
		if (success)
			clientHandler.setUsername(username);
	}

	@Override
	public short getBaseMessageID()
	{
		return Client.LOGIN_REQ_ID;
	}

}
