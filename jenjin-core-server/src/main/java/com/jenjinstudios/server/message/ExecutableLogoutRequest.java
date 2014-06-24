package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.sql.SQLConnector;

/**
 * Executable message to handle client logging out.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutRequest extends ServerExecutableMessage
{
	/** The SQLHandler used to log out the client. */
	private final SQLConnector sqlConnector;

	/**
	 * Construct a new ExecutableLogoutRequest.
	 * @param clientHandler The client handler which created this message.
	 * @param message The message used to create this ExecutableMessage.
	 */
	public ExecutableLogoutRequest(ClientHandler clientHandler, Message message) {
		super(clientHandler, message);
		sqlConnector = clientHandler.getServer().getSqlConnector();
	}

	@Override
	public void runSynced() {
	}

	@Override
	public void runASync() {
		ClientHandler handler = getClientHandler();
		User user = handler.getUser();
		if (sqlConnector == null || user == null)
			return;
		String username = user.getUsername();
		boolean loggedOut = sqlConnector.logOutUser(username);
		handler.sendLogoutStatus(loggedOut);
		if (loggedOut)
		{
			handler.getServer().associateUsernameWithClientHandler(username, null);
			handler.setUser(null);
		}
	}

}
