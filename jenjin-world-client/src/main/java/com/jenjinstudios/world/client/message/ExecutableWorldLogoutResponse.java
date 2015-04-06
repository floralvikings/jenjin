package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClientMessageContext;

/**
 * Handles logout responses.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutResponse extends WorldClientExecutableMessage<WorldClientMessageContext>
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldLogoutResponse(Message message, WorldClientMessageContext context) {
		super(message, context);
	}

	@Override
	public Message execute() {
		getContext().getLoginTracker().setLoggedIn(!((boolean) getMessage().getArgument("success")));
		return null;
	}
}
