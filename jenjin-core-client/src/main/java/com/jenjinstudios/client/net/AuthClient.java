package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

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

    public AuthClient(MessageIO messageIO, ClientUser user) {
        super(messageIO);
        this.loginTracker = new LoginTracker(this);
        this.user = user;
    }

    /**
     * Generate a LogoutRequest message.
     *
     * @return The LogoutRequestMessage.
     */
    public static Message generateLogoutRequest() {
        return MessageRegistry.getInstance().createMessage
              ("LogoutRequest");
    }

    /**
     * Generate a LoginRequest message.  This message will be encrypted if possible.
     *
     * @param user The User for which to generate the login request.
     * @return The LoginRequest message.
     */
    public static Message generateLoginRequest(ClientUser user) {// Create the login request.
        Message loginRequest = MessageRegistry.getInstance().createMessage("LoginRequest");
        loginRequest.setArgument("username", user.getUsername());
        loginRequest.setArgument("password", user.getPassword());
        return loginRequest;
    }

    public LoginTracker getLoginTracker() { return loginTracker; }

    /**
     * Get the username of this client.
     *
     * @return The username of this client.
     */
    public ClientUser getUser() { return user; }

}
