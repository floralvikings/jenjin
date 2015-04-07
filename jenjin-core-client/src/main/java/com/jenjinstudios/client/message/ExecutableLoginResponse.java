package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.ClientMessageContext;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;

/**
 * This class is used to respond to a LoginResponse.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginResponse extends ExecutableMessage<ClientMessageContext>
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableLoginResponse(Message message, ClientMessageContext context) {
		super(message, context);
	}

    @Override
	public Message execute() {
		getContext().getLoginTracker().setLoggedIn((boolean) getMessage().getArgument("success"));
		getContext().getLoginTracker().setWaitingForResponse(false);
		if (getContext().getLoginTracker().isLoggedIn())
		{
			getContext().getLoginTracker().setLoggedInTime((long) getMessage().getArgument("loginTime"));
		}
		return null;
	}

}
