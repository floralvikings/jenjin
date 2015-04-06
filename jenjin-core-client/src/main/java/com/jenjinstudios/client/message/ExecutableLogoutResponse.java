package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.Client;
import com.jenjinstudios.client.net.ClientMessageContext;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;

/**
 * This class responds to a LogoutResponse message.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutResponse extends ExecutableMessage<ClientMessageContext>
{
    /**
     * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this class.
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableLogoutResponse(Client client, Message message, ClientMessageContext context) {
		super(message, context);
	}

    @Override
	public Message execute() {
		getContext().getLoginTracker().setLoggedIn(!((boolean) getMessage().getArgument("success")));
		return null;
	}

}
