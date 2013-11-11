package com.jenjinstudios.message;

import com.jenjinstudios.net.AuthClient;

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
	public void runSynced() {
		getClient().setReceivedLogoutResponse(true);
		getClient().setLoggedIn(!((boolean) getMessage().getArgument("success")));
	}

	@Override
	public void runASync() {
	}

}
