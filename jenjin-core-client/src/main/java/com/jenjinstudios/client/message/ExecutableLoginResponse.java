package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;

/**
 * This class is used to respond to a LoginResponse.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginResponse extends ExecutableMessage<MessageContext>
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableLoginResponse(AuthClient client, Message message, MessageContext context) {
		super(client, message, context);
	}

    @Override
	public Message execute() {
		AuthClient client = (AuthClient) getThreadPool();
		client.getLoginTracker().setLoggedIn((boolean) getMessage().getArgument("success"));
		if (client.getLoginTracker().isLoggedIn())
		{
			client.getLoginTracker().setLoggedInTime((long) getMessage().getArgument("loginTime"));
			client.setName("Client: " + client.getUser().getUsername());
		}
		return null;
	}

}
