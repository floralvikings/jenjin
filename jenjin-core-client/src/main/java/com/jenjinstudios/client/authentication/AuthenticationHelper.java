package com.jenjinstudios.client.authentication;

import com.jenjinstudios.client.net.Client;
import com.jenjinstudios.client.net.ClientMessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Group of utility methods to help the authentication process.
 *
 * @author Caleb Brinkman
 */
public final class AuthenticationHelper
{
	private static final int THIRTY_SECONDS = 30000;
	private static final Logger LOGGER = Logger.getLogger(AuthenticationHelper.class.getName());

	private AuthenticationHelper() {}

	/**
	 * Send a login request and await the response.
	 *
	 * @param client The client to send the message.
	 *
	 * @return Whether the login was successful.
	 */
	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	public static <T extends ClientMessageContext> boolean loginAndWait(Client<T> client) {
		sendLoginRequest(client);
		long startTime = System.currentTimeMillis();
		while (client.getLoginTracker().isWaitingForResponse() && ((System.currentTimeMillis() - startTime) <
			  THIRTY_SECONDS))
		{
			waitTenMillis();
		}
		return client.getLoginTracker().isLoggedIn();
	}

	/**
	 * Send a logout request and block execution until the response is received.
	 *
	 * @param client The client sending the message.
	 */
	public static <T extends ClientMessageContext> void logoutAndWait(Client<T> client) {
		sendLogoutRequest(client);
		long startTime = System.currentTimeMillis();
		while (client.getLoginTracker().isWaitingForResponse() && ((System.currentTimeMillis() - startTime) <
			  THIRTY_SECONDS))
		{
			waitTenMillis();
		}
	}

	/**
	 * Generate a LoginRequest message.  This message will be encrypted if possible.
	 *
	 * @param user The User for which to generate the login request.
	 *
	 * @return The LoginRequest message.
	 */
	public static Message generateLoginRequest(User user) {// Create the login request.
		Message loginRequest = MessageRegistry.getGlobalRegistry().createMessage("LoginRequest");
		loginRequest.setArgument("username", user.getUsername());
		loginRequest.setArgument("password", user.getPassword());
		return loginRequest;
	}

	/**
	 * Generate a LogoutRequest message.
	 *
	 * @return The LogoutRequestMessage.
	 */
	public static Message generateLogoutRequest() {
		return MessageRegistry.getGlobalRegistry().createMessage
			  ("LogoutRequest");
	}

	private static <T extends ClientMessageContext> void sendLoginRequest(Client<T> client) {
		client.getLoginTracker().setWaitingForResponse(true);
		Message message = generateLoginRequest(client.getMessageContext().getUser());
		client.enqueueMessage(message);
	}

	private static <T extends ClientMessageContext> void sendLogoutRequest(Client<T> client) {
		client.getLoginTracker().setWaitingForResponse(true);
		Message message = generateLogoutRequest();
		client.enqueueMessage(message);
	}

	private static void waitTenMillis() {
		try
		{
			Thread.sleep(10);
		} catch (InterruptedException e)
		{
			LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
		}
	}
}
