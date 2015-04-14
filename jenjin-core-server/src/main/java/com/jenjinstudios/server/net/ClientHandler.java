package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.server.authentication.AuthenticationException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler<T extends ServerMessageContext> extends Connection<T>
{
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

	public ClientHandler(MessageStreamPair messageStreamPair, T context) {
		super(messageStreamPair, context);
		getMessageContext().setName("ClientHandler " + getId());
	}

	@Override
    public void shutdown() {
		if (getMessageContext().getUser() != null)
		{
            try
            {
				getMessageContext().getAuthenticator().logOutUser(getMessageContext().getUser().getUsername());
			} catch (AuthenticationException e)
			{
                LOGGER.log(Level.WARNING, "Unable to perform emergency logout.", e);
            }
        }
		super.shutdown();
	}

}
