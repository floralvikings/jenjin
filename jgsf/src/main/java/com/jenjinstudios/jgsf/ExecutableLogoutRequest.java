package com.jenjinstudios.jgsf;

import com.jenjinstudios.message.LogoutRequest;

/**
 * Executable message to handle client logging out.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutRequest extends ExecutableMessage
{
	/** The client handler which created this message. */
	private final ClientHandler clientHandler;
	/** The SQLHandler used to log out the client. */
	private final SQLHandler sqlHandler;
	/** The LogoutRequest for which this class is created. */
	private final LogoutRequest logoutRequest;

	/**
	 * Construct a new ExecutableLogoutRequest.
	 *
	 * @param clientHandler The client handler which created this message.
	 * @param logoutRequest The message used to create this ExecutableMessage.
	 */
	public ExecutableLogoutRequest(ClientHandler clientHandler, LogoutRequest logoutRequest)
	{
		super(clientHandler, logoutRequest);
		this.clientHandler = clientHandler;
		sqlHandler = clientHandler.getServer().getSqlHandler();
		this.logoutRequest = logoutRequest;
	}

	@Override
	public void runSynced()
	{
	}

	@Override
	public void runASync()
	{
		if (sqlHandler == null || !clientHandler.isLoggedIn())
			return;
		clientHandler.queueLogoutStatus(sqlHandler.logOutUser(clientHandler.getUsername()));
	}

}
