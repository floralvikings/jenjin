package com.jenjinstudios.jgsf;

import com.jenjinstudios.jgcf.Client;
import com.jenjinstudios.message.BaseMessage;

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

	/**
	 * Construct a new ExecutableLogoutRequest.
	 *
	 * @param clientHandler The client handler which created this message.
	 * @param message The message used to create this ExecutableMessage.
	 */
	public ExecutableLogoutRequest(ClientHandler clientHandler, BaseMessage message)
	{
		super(clientHandler, message);
		this.clientHandler = clientHandler;
		sqlHandler = clientHandler.getServer().getSqlHandler();
		/* The LogoutRequest for which this class is created. */
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

	@Override
	public short getBaseMessageID()
	{
		return Client.LOGOUT_REQ_ID;
	}

}
