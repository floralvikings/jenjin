package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.Client;

/**
 * This class is used to respond to a LoginResponse.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginResponse extends ClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given BaseMessage.
	 *
	 * @param client The client invoking this message.
	 * @param message The BaseMessage.
	 */
	public ExecutableLoginResponse(Client client, BaseMessage message)
	{
		super(client, message);
	}

	@Override
	public void runSynced()
	{
		Client client = getClient();
		client.setReceivedLoginResponse(true);
		client.setLoggedIn((boolean) getMessage().getArgs()[0]);
		if (!client.isLoggedIn())
			return;
		client.setLoggedInTime((long) getMessage().getArgs()[1]);
		client.setName("Client: " + client.getUsername());
	}

	@Override
	public void runASync()
	{
	}

	@Override
	public short getBaseMessageID()
	{
		return Client.LOGIN_RESP_ID;
	}
}
