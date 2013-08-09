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
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableLoginResponse(Client client, Message message)
	{
		super(client, message);
	}

	@Override
	public void runSynced()
	{
		Client client = getClient();
		client.setReceivedLoginResponse(true);
		client.setLoggedIn((boolean) getMessage().getArgument("success"));
		if (!client.isLoggedIn())
			return;
		client.setLoggedInTime((long) getMessage().getArgument("loginTime"));
		client.setName("Client: " + client.getUsername());
	}

	@Override
	public void runASync()
	{
	}

}
