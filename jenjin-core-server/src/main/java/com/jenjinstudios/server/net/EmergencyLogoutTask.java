package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.concurrency.ShutdownTask;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs a logout on a client handler when shutting down.
 *
 * @author Caleb Brinkman
 */
public class EmergencyLogoutTask<U extends User, T extends ServerMessageContext<U>> implements ShutdownTask<T>
{
	private static final Logger LOGGER = Logger.getLogger(EmergencyLogoutTask.class.getName());

	@Override
	public void shutdown(Connection<? extends T> connection) {
		ServerMessageContext<U> context = connection.getMessageContext();
		U user = context.getUser();
		if (user != null)
		{
			try
			{
				context.getAuthenticator().logOutUser(user);
			} catch (AuthenticationException e)
			{
				LOGGER.log(Level.WARNING, "Unable to perform emergency logout.", e);
			}
		}
	}
}
