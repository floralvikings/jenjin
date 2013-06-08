package com.jenjinstudios.jgsf;

import com.jenjinstudios.message.LoginRequest;
import com.jenjinstudios.security.Hasher;

/**
 * Executes the necessary actions to deal with a login response.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginRequest extends ExecutableMessage
{
	/** The login request to be handled by this executable message. */
	private final LoginRequest loginRequest;
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
	public ExecutableLoginRequest(ClientHandler clientHandler, LoginRequest loginRequest)
	{
		super(clientHandler, loginRequest);
		this.loginRequest = loginRequest;
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
		String hashPass = Hasher.getHashedString(loginRequest.password);
		boolean success = sqlHandler.logInUser(loginRequest.username, hashPass);
		clientHandler.queueLoginStatus(success);
		if (success)
			clientHandler.setUsername(loginRequest.username);
	}

}
