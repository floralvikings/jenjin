package com.jenjinstudios.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.client.net.AuthClient;

/**
 * This class responds to a LogoutResponse message.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutResponse extends AuthClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this class.
	 * @param message The Message.
	 */
	public ExecutableLogoutResponse(AuthClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runDelayed() {
		getClient().setWaitingForLogoutResponse(false);
		getClient().setLoggedIn(!((boolean) getMessage().getArgument("success")));
	}

	@Override
	public void runImmediate() {
	}

}
