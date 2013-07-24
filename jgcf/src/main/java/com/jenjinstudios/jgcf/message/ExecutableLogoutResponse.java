package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.Client;

/**
 * This class responds to a LogoutResponse message.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutResponse extends ClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given BaseMessage.
	 *
	 * @param client The client invoking this class.
	 * @param message The BaseMessage.
	 */
	public ExecutableLogoutResponse(Client client, BaseMessage message)
	{
		super(client, message);
	}

	@Override
	public void runSynced()
	{
		getClient().setReceivedLogoutResponse(true);
		getClient().setLoggedIn(!((boolean) getMessage().getArgs()[0]));
	}

	@Override
	public void runASync()
	{
	}

	@Override
	public short getBaseMessageID()
	{
		return Client.LOGOUT_RESP_ID;
	}
}
