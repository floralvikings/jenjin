package com.jenjinstudios.client.net;

import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to track the status of login requests and responses.
 *
 * @author Caleb Brinkman
 */
public class LoginTracker
{
    private static final Logger LOGGER = Logger.getLogger(LoginTracker.class.getName());
    private static final int MILLIS_IN_30_SECONDS = 30000;
    private volatile boolean loggedIn;
    private volatile boolean waitingForResponse;
    private long loggedInTime;
    private final AuthClient client;


    public LoginTracker(AuthClient client) { this.client = client; }

    public boolean isLoggedIn() { return loggedIn; }

    public void setLoggedIn(boolean loggedIn) {
        waitingForResponse = false;
        this.loggedIn = loggedIn;
    }

    public long getLoggedInTime() { return loggedInTime; }

    public void setLoggedInTime(long loggedInTime) { this.loggedInTime = loggedInTime; }

    protected void sendLoginRequest() {
        waitingForResponse = true;
        Message message = AuthClient.generateLoginRequest(client.getUser());
        client.getMessageIO().queueOutgoingMessage(message);
    }

    public boolean sendLoginRequestAndWaitForResponse() {
        sendLoginRequest();
        long startTime = System.currentTimeMillis();
        while (waitingForResponse && ((System.currentTimeMillis() - startTime) < (long) MILLIS_IN_30_SECONDS))
        {
            waitTenMillis();
        }
        return loggedIn;
    }

    public void sendLogoutRequestAndWaitForResponse() {
        sendLogoutRequest();
        long startTime = System.currentTimeMillis();
        while (waitingForResponse && ((System.currentTimeMillis() - startTime) < (long) MILLIS_IN_30_SECONDS))
        {
            waitTenMillis();
        }
    }

    private void sendLogoutRequest() {
        waitingForResponse = true;
        Message message = AuthClient.generateLogoutRequest();
        client.getMessageIO().queueOutgoingMessage(message);
    }

    private void waitTenMillis() {
        try
        {
            Thread.sleep(10);
        } catch (InterruptedException e)
        {
            LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
        }
    }

}
