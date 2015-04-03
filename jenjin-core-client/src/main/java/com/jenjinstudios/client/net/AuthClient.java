package com.jenjinstudios.client.net;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageStreamPair;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code AuthClient} class is a {@code Client} with the ability to store user information and attempt to log into a
 * {@code Server} using a username and password.
 *
 * @author Caleb Brinkman
 */
public class AuthClient extends Client
{
	private static final Logger LOGGER = Logger.getLogger(AuthClient.class.getName());
	static final int THIRTY_SECONDS = 30000;

	private final ClientUser user;
	private final LoginTracker loginTracker;

    /**
     * Construct a new client with authentication abilities.
     *
     * @param messageStreamPair The MessageIO used by this client to communicate with a server.
     * @param user The user which this client will attempt to authenticate.
     */
    public AuthClient(MessageStreamPair messageStreamPair, ClientUser user) {
        super(messageStreamPair);
		this.loginTracker = new LoginTracker();
		this.user = user;
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

    /**
     * Generate a LoginRequest message.  This message will be encrypted if possible.
     *
     * @param user The User for which to generate the login request.
     * @return The LoginRequest message.
     */
    public static Message generateLoginRequest(ClientUser user) {// Create the login request.
		Message loginRequest = MessageRegistry.getGlobalRegistry().createMessage("LoginRequest");
		loginRequest.setArgument("username", user.getUsername());
        loginRequest.setArgument("password", user.getPassword());
        return loginRequest;
    }

	/**
	 * Send a logout request and block execution until the response is received.
	 */
	public void logoutAndWait() {
		sendLogoutRequest();
		long startTime = System.currentTimeMillis();
		while (loginTracker.isWaitingForResponse() && ((System.currentTimeMillis() - startTime) < THIRTY_SECONDS))
		{
			waitTenMillis();
		}
	}

	void sendLogoutRequest() {
		loginTracker.setWaitingForResponse(true);
		Message message = generateLogoutRequest();
		enqueueMessage(message);
	}

	/**
	 * Send a login request and await the response.
	 *
	 * @return Whether the login was successful.
	 */
	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	public boolean loginAndWait() {
		sendLoginRequest();
		long startTime = System.currentTimeMillis();
		while (loginTracker.isWaitingForResponse() && ((System.currentTimeMillis() - startTime) < THIRTY_SECONDS))
		{
			waitTenMillis();
		}
		return loginTracker.isLoggedIn();
	}

	static void waitTenMillis() {
		try
		{
			Thread.sleep(10);
		} catch (InterruptedException e)
		{
			LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
		}
	}

	/**
	 * Send a login request.
	 */
	protected void sendLoginRequest() {
		loginTracker.setWaitingForResponse(true);
		Message message = generateLoginRequest(user);
		enqueueMessage(message);
	}

	/**
	 * Get the LoginTracker managed by this client.
     *
     * @return The login tracker managed by this client.
     */
    public LoginTracker getLoginTracker() { return loginTracker; }

    /**
     * Get the username of this client.
     *
     * @return The username of this client.
     */
    public ClientUser getUser() { return user; }

}
