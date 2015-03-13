package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.IUser;
import com.jenjinstudios.server.database.DbException;
import com.jenjinstudios.server.net.ClientHandler;

/**
 * Executable message to handle client logging out.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutRequest extends ServerExecutableMessage
{
	/** The SQLHandler used to log out the client. */
	private final Authenticator authenticator;

	/**
	 * Construct a new ExecutableLogoutRequest.
	 * @param clientHandler The client handler which created this message.
	 * @param message The message used to create this ExecutableMessage.
	 */
	public ExecutableLogoutRequest(ClientHandler clientHandler, Message message) {
		super(clientHandler, message);
		authenticator = clientHandler.getServer().getAuthenticator();
	}

	@Override
	public void runDelayed() {
	}

	@Override
	public void runImmediate() {
		ClientHandler handler = getClientHandler();
		IUser user = handler.getUser();
		if (authenticator != null && user != null)
		{
			String username = user.getUsername();
			try
			{
				user = authenticator.logOutUser(username);
				handler.sendLogoutStatus(!user.isLoggedIn());
				handler.setUser(null);
			} catch (DbException e)
			{
				handler.sendLogoutStatus(false);
			}
		}
	}
}
