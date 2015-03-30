package com.jenjinstudios.client.net;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageIO;
import com.jenjinstudios.core.io.MessageRegistry;

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
    private final ClientUser user;
    private final LoginTracker loginTracker;

    /**
     * Construct a new client with authentication abilities.
     *
     * @param messageIO The MessageIO used by this client to communicate with a server.
     * @param user The user which this client will attempt to authenticate.
     */
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

    /**
     * Used to track the status of login requests and responses.
     *
     * @author Caleb Brinkman
     */
    public static class LoginTracker
    {
        private static final Logger LOGGER = Logger.getLogger(LoginTracker.class.getName());
        private static final int MILLIS_IN_30_SECONDS = 30000;
        private volatile boolean loggedIn;
        private volatile boolean waitingForResponse;
        private long loggedInTime;
        private final AuthClient client;

        /**
         * Construct a new LoginTracker working for the given client.
         *
         * @param client The client using this LoginTracker.
         */
        public LoginTracker(AuthClient client) { this.client = client; }

        /**
         * Returns whether this login tracker has received a successful login response.
         *
         * @return Whether this login tracker has received a successful login response.
         */
        public boolean isLoggedIn() { return loggedIn; }

        /**
         * Set when a successful login response has been received.
         *
         * @param loggedIn Whether the login was successful.
         */
        public void setLoggedIn(boolean loggedIn) {
            waitingForResponse = false;
            this.loggedIn = loggedIn;
        }

        /**
         * Get the time at which the login was performed.
         *
         * @return The time at which the login was performed.
         */
        public long getLoggedInTime() { return loggedInTime; }

        /**
         * Set the time at which the login was performed.
         *
         * @param loggedInTime The time at which the login was performed.
         */
        public void setLoggedInTime(long loggedInTime) { this.loggedInTime = loggedInTime; }

        /**
         * Send a login request for this tracker's client.
         */
        protected void sendLoginRequest() {
            waitingForResponse = true;
            Message message = generateLoginRequest(client.getUser());
			client.enqueueMessage(message);
		}

        /**
         * Send a login request and await the response.
         *
         * @return Whether the login was successful.
         */
        @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
        public boolean sendLoginRequestAndWaitForResponse() {
            sendLoginRequest();
            long startTime = System.currentTimeMillis();
            while (waitingForResponse && ((System.currentTimeMillis() - startTime) < MILLIS_IN_30_SECONDS))
            {
                waitTenMillis();
            }
            return loggedIn;
        }

        /**
         * Send a logout request and block execution until the response is received.
         */
        public void sendLogoutRequestAndWaitForResponse() {
            sendLogoutRequest();
            long startTime = System.currentTimeMillis();
            while (waitingForResponse && ((System.currentTimeMillis() - startTime) < MILLIS_IN_30_SECONDS))
            {
                waitTenMillis();
            }
        }

        private void sendLogoutRequest() {
            waitingForResponse = true;
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
}
