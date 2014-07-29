package com.jenjinstudios.client.net;

import com.jenjinstudios.client.message.ClientMessageFactory;
import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public class LoginTracker
{
	private static final Logger LOGGER = Logger.getLogger(LoginTracker.class.getName());
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

	public void sendLoginRequest() {
		waitingForResponse = true;
		Message message = ((ClientMessageFactory) client.getMessageFactory()).generateLoginRequest(client.getUser());
		client.queueOutgoingMessage(message);
	}

	public boolean sendLoginRequestAndWaitForResponse(long timeout) {
		sendLoginRequest();
		long startTime = System.currentTimeMillis();
		while (waitingForResponse && System.currentTimeMillis() - startTime < timeout)
		{
			wait(100);
		}
		return loggedIn;
	}

	public void sendLogoutRequestAndWaitForResponse(long timeout) {
		sendLogoutRequest();
		long startTime = System.currentTimeMillis();
		while (waitingForResponse && System.currentTimeMillis() - startTime < timeout)
		{
			wait(100);
		}
	}

	private void sendLogoutRequest() {
		waitingForResponse = true;
		Message message = ((ClientMessageFactory) client.getMessageFactory()).generateLogoutRequest();
		client.queueOutgoingMessage(message);
	}

	private void wait(int waitTime) {
		try
		{
			Thread.sleep(waitTime);
		} catch (InterruptedException e)
		{
			LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.");
		}
	}

}
