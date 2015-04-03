package com.jenjinstudios.client.net;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageStreamPair;

/**
 * The {@code AuthClient} class is a {@code Client} with the ability to store user information and attempt to log into a
 * {@code Server} using a username and password.
 *
 * @author Caleb Brinkman
 */
public class AuthClient extends Client
{
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
        this.loginTracker = new LoginTracker(this);
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
	 * Send a login request for this tracker's client.
	 *
	 * @param loginTracker The login tracker used to track the login response.
	 * @param client The client.
	 */
	protected static void sendLoginRequest(LoginTracker loginTracker, AuthClient client) {
		loginTracker.setWaitingForResponse(true);
		Message message = generateLoginRequest(client.getUser());
		client.enqueueMessage(message);
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
