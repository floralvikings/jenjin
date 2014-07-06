package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;

/**
 * Handles logout responses.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutResponse extends WorldClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableWorldLogoutResponse(WorldClient client, Message message) {
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
