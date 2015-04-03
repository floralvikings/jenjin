package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.Client;
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
     * @param client The client invoking this message.
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableLoginResponse(Client client, Message message, ClientMessageContext context) {
		super(client, message, context);
	}

    @Override
	public Message execute() {
		Client client = (Client) getThreadPool();
		client.getLoginTracker().setLoggedIn((boolean) getMessage().getArgument("success"));
		if (client.getLoginTracker().isLoggedIn())
		{
			client.getLoginTracker().setLoggedInTime((long) getMessage().getArgument("loginTime"));
			client.setName("Client: " + client.getUser().getUsername());
		}
		return null;
	}

}
