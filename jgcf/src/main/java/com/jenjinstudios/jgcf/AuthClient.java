package com.jenjinstudios.jgcf;

import com.jenjinstudios.message.Message;

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
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(AuthClient.class.getName());
	/** The username this client will use when logging in. */
	private String username;
	/** The password this client will use when logging in. */
	private String password;
	/** Whether the user is logged in. */
	private boolean loggedIn;
	/** The time at which this client was successfully logged in. */
	private long loggedInTime;
	/** flags whether the login response has been received. */
	private volatile boolean receivedLoginResponse;
	/** flags whether the logout response has been received. */
	private volatile boolean receivedLogoutResponse;

	/**
	 * Construct a client connecting to the given address over the given port.
	 *
	 * @param address The address to which this client will attempt to connect.
	 * @param port The port over which this client will attempt to connect.
	 * @param username The username that will be used by this client.
	 * @param password The password that will be used by this client.
	 */
	public AuthClient(String address, int port, String username, String password)
	{
		super(address, port);
		this.username = username;
		this.password = password;
	}

	/** Queue a message to log into the server with the given username and password, and wait for the response. */
	public void sendLoginRequest()
	{
		if (username == null || password == null)
		{
			LOGGER.log(Level.WARNING, "Attempted to login without username or password");
			return;
		}

		// Create the login request.
		Message loginRequest = new Message("LoginRequest");
		loginRequest.setArgument("username", username);
		loginRequest.setArgument("password", password);

		// Send the request, continue when the response is received.
		setReceivedLoginResponse(false);
		sendMessage(loginRequest);
		while (!hasReceivedLoginResponse())
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
	}

	/** Queue a message to log the user out of the server. */
	public void sendLogoutRequest()
	{
		// Create the message.
		Message logoutRequest = new Message("LogoutRequest");

		// Send the request, continue when response is received.
		setReceivedLogoutResponse(false);
		sendMessage(logoutRequest);
		while (!hasReceivedLogoutResponse())
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
	}

	/**
	 * Get the username of this client.
	 *
	 * @return The username of this client.
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Set whether this client has received a login response.
	 *
	 * @param receivedLoginResponse Whether this client has received a login response.
	 */
	public void setReceivedLoginResponse(boolean receivedLoginResponse)
	{
		this.receivedLoginResponse = receivedLoginResponse;
	}

	/**
	 * Get whether this client has received a login response.
	 *
	 * @return Whether the client has received a login response.
	 */
	public boolean hasReceivedLoginResponse()
	{
		return receivedLoginResponse;
	}

	/**
	 * Set whether this client has received a logout response.
	 *
	 * @param receivedLogoutResponse Whether this client has received a logout response.
	 */
	public void setReceivedLogoutResponse(boolean receivedLogoutResponse)
	{
		this.receivedLogoutResponse = receivedLogoutResponse;
	}

	/**
	 * Get whether this client has received a logout response.
	 *
	 * @return Whether this client has received a logout response.
	 */
	public boolean hasReceivedLogoutResponse()
	{
		return receivedLogoutResponse;
	}

	/**
	 * Get whether this client is logged in.
	 *
	 * @return true if this client has received a successful LoginResponse
	 */
	public boolean isLoggedIn()
	{
		return loggedIn;
	}

	/**
	 * Set whether this client is logged in.
	 *
	 * @param l Whether this client is logged in.
	 */
	public void setLoggedIn(boolean l)
	{
		loggedIn = l;
	}

	/**
	 * Get the time at which this client was successfully logged in.
	 *
	 * @return The time of the start of the server cycle during which this client was logged in.
	 */
	public long getLoggedInTime()
	{
		return loggedInTime;
	}

	/**
	 * Set the logged in time for this client.
	 *
	 * @param loggedInTime The logged in time for this client.
	 */
	public void setLoggedInTime(long loggedInTime)
	{
		this.loggedInTime = loggedInTime;
	}
}
