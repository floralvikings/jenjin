package com.jenjinstudios.client.net;

/**
 * Used to track the status of login requests and responses.
 *
 * @author Caleb Brinkman
 */
public class LoginTracker
{
	private volatile boolean loggedIn;
	private volatile boolean waitingForResponse;
	private long loggedInTime;

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
	 * Get whether this tracker is waiting for a response.
	 *
	 * @return Whether this tracker is waiting for a response.
	 */
	public boolean isWaitingForResponse() { return waitingForResponse; }

	/**
	 * Set whether this tracker is waiting for a response.
	 *
	 * @param waitingForResponse Whether this tracker is waiting for a response.
	 */
	public void setWaitingForResponse(boolean waitingForResponse) { this.waitingForResponse = waitingForResponse; }

}
