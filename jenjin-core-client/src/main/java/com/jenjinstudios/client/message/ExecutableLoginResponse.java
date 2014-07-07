package com.jenjinstudios.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.client.net.AuthClient;

/**
 * This class is used to respond to a LoginResponse.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginResponse extends AuthClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableLoginResponse(AuthClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runDelayed() {
		AuthClient client = getClient();
		client.setWaitingForLoginResponse(false);
		client.setLoggedIn((boolean) getMessage().getArgument("success"));
		if (!client.isLoggedIn())
			return;
		client.setLoggedInTime((long) getMessage().getArgument("loginTime"));
		client.setName("Client: " + client.getUser().getUsername());
	}

	@Override
	public void runImmediate() {
	}

}
