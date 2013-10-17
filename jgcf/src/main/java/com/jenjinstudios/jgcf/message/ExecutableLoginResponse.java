package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.AuthClient;
import com.jenjinstudios.message.Message;

/**
 * This class is used to respond to a LoginResponse.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginResponse extends AuthClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableLoginResponse(AuthClient client, Message message)
	{
		super(client, message);
	}

	@Override
	public void runSynced()
	{
		AuthClient client = getClient();
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
