package com.jenjinstudios.net;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.util.ClientMessageFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code AuthClient} class is a {@code Client} with the ability to store user information and attempt to log into a
 * {@code Server} using a username and password.
 * @author Caleb Brinkman
 */
public class AuthClient extends Client
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(AuthClient.class.getName());
	/** The number of milliseconds before a blocking method should time out. */
	public static long TIMEOUT_MILLIS = 30000;
	/** The username this client will use when logging in. */
	private String username;
	/** The password this client will use when logging in. */
	private String password;
	/** Whether the user is logged in. */
	private boolean loggedIn;
	/** The time at which this client was successfully logged in. */
	private long loggedInTime;
	/** flags whether the login response has been received. */
	private volatile boolean waitingForLoginResponse;
	/** flags whether the logout response has been received. */
	private volatile boolean waitingForLogoutResponse;

	/**
	 * Construct a client connecting to the given address over the given port.
	 * @param address The address to which this client will attempt to connect.
	 * @param port The port over which this client will attempt to connect.
	 * @param username The username that will be used by this client.
	 * @param password The password that will be used by this client.
	 * @throws java.security.NoSuchAlgorithmException If there is an error generating encryption keys.
	 * @throws java.io.IOException If there is an IO exception when reading XML files.
	 * @throws javax.xml.parsers.ParserConfigurationException If there is an error parsing XML files.
	 * @throws org.xml.sax.SAXException If there is an error parsing XML files.
	 */
	public AuthClient(String address, int port, String username, String password) throws NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException {
		super(address, port);
		this.username = username;
		this.password = password;
	}

	/**
	 * Queue a message to log into the server with the given username and password, and wait for the response.
	 * @return If the login was successful.
	 */
	public boolean sendBlockingLoginRequest() {
		sendLoginRequest();
		long startTime = System.currentTimeMillis();
		long timePast = System.currentTimeMillis() - startTime;
		while (isWaitingForLoginResponse() && (timePast < TIMEOUT_MILLIS))
		{
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
			timePast = System.currentTimeMillis() - startTime;
		}
		return isLoggedIn();
	}

	/**
	 * Get whether this client is logged in.
	 * @return true if this client has received a successful LoginResponse
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * Set whether this client is logged in.
	 * @param l Whether this client is logged in.
	 */
	public void setLoggedIn(boolean l) {
		loggedIn = l;
	}

	/** Send a login request to the server. */
	private void sendLoginRequest() {
		if (username == null || password == null)
		{
			throw new IllegalStateException("Attempted to login without username or password");
		}
		Message loginRequest = ClientMessageFactory.generateLoginRequest(this, username, password);

		// Send the request, continue when the response is received.
		setWaitingForLoginResponse(true);
		queueMessage(loginRequest);
	}

	/**
	 * Set whether this client has received a login response.
	 * @param waitingForLoginResponse Whether this client has received a login response.
	 */
	public void setWaitingForLoginResponse(boolean waitingForLoginResponse) {
		this.waitingForLoginResponse = waitingForLoginResponse;
	}

	/**
	 * Get whether this client has received a login response.
	 * @return Whether the client has received a login response.
	 */
	public boolean isWaitingForLoginResponse() {
		return waitingForLoginResponse;
	}

	/**
	 * Queue a message to log the user out of the server.
	 * @return Whether the logout request was successful.
	 */
	public boolean sendBlockingLogoutRequest() {
		sendLogoutRequest();
		long startTime = System.currentTimeMillis();
		long timePast = System.currentTimeMillis() - startTime;
		while (isWaitingForLogoutResponse() && (timePast < TIMEOUT_MILLIS))
		{
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
			timePast = System.currentTimeMillis() - startTime;
		}
		return !isLoggedIn();
	}

	/** Send a logout request to the server. */
	protected void sendLogoutRequest() {
		Message logoutRequest = ClientMessageFactory.generateLogoutRequest(this);

		// Send the request, continue when response is received.
		setWaitingForLogoutResponse(true);
		queueMessage(logoutRequest);
	}

	/**
	 * Set whether this client has received a logout response.
	 * @param waitingForLogoutResponse Whether this client has received a logout response.
	 */
	public void setWaitingForLogoutResponse(boolean waitingForLogoutResponse) {
		this.waitingForLogoutResponse = waitingForLogoutResponse;
	}

	/**
	 * Get whether this client has received a logout response.
	 * @return Whether this client has received a logout response.
	 */
	public boolean isWaitingForLogoutResponse() {
		return waitingForLogoutResponse;
	}

	/**
	 * Get the username of this client.
	 * @return The username of this client.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the time at which this client was successfully logged in.
	 * @return The time of the start of the server cycle during which this client was logged in.
	 */
	public long getLoggedInTime() {
		return loggedInTime;
	}

	/**
	 * Set the logged in time for this client.
	 * @param loggedInTime The logged in time for this client.
	 */
	public void setLoggedInTime(long loggedInTime) {
		this.loggedInTime = loggedInTime;
	}
}
