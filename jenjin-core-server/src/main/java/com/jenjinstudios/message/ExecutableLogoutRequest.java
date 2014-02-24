package com.jenjinstudios.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.ClientHandler;
import com.jenjinstudios.sql.SQLHandler;

/**
 * Executable message to handle client logging out.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableLogoutRequest extends ServerExecutableMessage
{
	/** The SQLHandler used to log out the client. */
	private final SQLHandler sqlHandler;

	/**
	 * Construct a new ExecutableLogoutRequest.
	 * @param clientHandler The client handler which created this message.
	 * @param message The message used to create this ExecutableMessage.
	 */
	public ExecutableLogoutRequest(ClientHandler clientHandler, Message message) {
		super(clientHandler, message);
		sqlHandler = clientHandler.getServer().getSqlHandler();
	}

	@Override
	public void runSynced() {
	}

	@Override
	public void runASync() {
		if (sqlHandler == null || !getClientHandler().isLoggedIn())
			return;
		getClientHandler().sendLogoutStatus(sqlHandler.logOutUser(getClientHandler().getUsername()));
	}

}
