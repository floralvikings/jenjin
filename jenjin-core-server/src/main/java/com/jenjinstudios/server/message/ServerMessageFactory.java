package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

/**
 * Used to generate messages for the Jenjin core server.
 *
 * @author Caleb Brinkman
 */
public final class ServerMessageFactory
{
	private ServerMessageFactory() {}

	/**
	 * Generate a LogoutResponse.
     *
     * @param success Whether the logout attempt was successful.
     *
     * @return The LogoutResponse.
     */
	public static Message generateLogoutResponse(boolean success) {
		Message logoutResponse = MessageRegistry.getInstance().createMessage("LogoutResponse");
        logoutResponse.setArgument("success", success);
        return logoutResponse;
    }

    /**
     * Generate a response to a login attempt.
     *
     * @param success Whether the login attempt was successful.
     * @param loggedInTime The time of the successful login.
     *
     * @return The LoginResponse message.
     */
	public static Message generateLoginResponse(boolean success, long loggedInTime) {
		Message loginResponse = MessageRegistry.getInstance().createMessage("LoginResponse");
        loginResponse.setArgument("success", success);
        loginResponse.setArgument("loginTime", loggedInTime);
        return loginResponse;
    }
}
