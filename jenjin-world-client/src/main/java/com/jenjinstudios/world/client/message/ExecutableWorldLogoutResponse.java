package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.net.ClientMessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;

/**
 * Handles logout responses.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutResponse extends WorldClientExecutableMessage<ClientMessageContext>
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldLogoutResponse(WorldClient client, Message message, ClientMessageContext context) {
		super(client, message, context);
	}

	@Override
	public Message execute() {
		getWorldClient().getLoginTracker().setLoggedIn(!((boolean) getMessage().getArgument("success")));
		return null;
	}
}
