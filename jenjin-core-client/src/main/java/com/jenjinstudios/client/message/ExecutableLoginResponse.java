package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;

/**
 * This class is used to respond to a LoginResponse.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLoginResponse extends ExecutableMessage
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
     */
    public ExecutableLoginResponse(AuthClient client, Message message) {
        super(client, message);
    }

    @Override
	public void execute() {
		AuthClient client = (AuthClient) getThreadPool();
		client.getLoginTracker().setLoggedIn((boolean) getMessage().getArgument("success"));
		if (client.getLoginTracker().isLoggedIn())
		{
			client.getLoginTracker().setLoggedInTime((long) getMessage().getArgument("loginTime"));
			client.setName("Client: " + client.getUser().getUsername());
		}
	}

}
