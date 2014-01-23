package com.jenjinstudios.world;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.AuthClient;
import com.jenjinstudios.world.state.MoveState;
import com.jenjinstudios.world.util.ClientMessageGenerator;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The WorldClient class is used to connect to a WorldServer and stores information about the environment immediately
 * surrounding the player.
 * @author Caleb Brinkman
 */
public class WorldClient extends AuthClient
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldClient.class.getName());
	/** The number of milliseconds before a blocking method should time out. */
	public static long TIMEOUT_MILLIS = 30000;
	/** The password used to login to the world. */
	private final String password;
	/** The world. */
	// TODO Make this read from server
	private World world;
	/** The actor representing the player controlled by this client. */
	private ClientPlayer player;

	/**
	 * Construct a client connecting to the given address over the given port.  This client <i>must</i> have a username and
	 * password.
	 * @param address The address to which this client will attempt to connect.
	 * @param port The port over which this client will attempt to connect.
	 * @param username The username that will be used by this client.
	 * @param password The password that will be used by this client.
	 * @throws java.security.NoSuchAlgorithmException If there is an error generating encryption keys.
	 */
	public WorldClient(String address, int port, String username, String password) throws NoSuchAlgorithmException {
		super(address, port, username, password);

		this.password = password;
	}

	@Override
	public boolean sendBlockingLoginRequest() {
		sendLoginRequest();
		long startTime = System.currentTimeMillis();
		long timepast = System.currentTimeMillis() - startTime;
		while (isWaitingForLoginResponse() && (timepast < TIMEOUT_MILLIS))
		{
			try
			{
				sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
			timepast = System.currentTimeMillis() - startTime;
		}
		return isLoggedIn();
	}

	/**
	 * Get the player associated with this client.
	 * @return The player (ClientActor) associated with this client.
	 */
	public ClientPlayer getPlayer() { return player; }

	/**
	 * Set the player being controlled by this client.
	 * @param player The player to be controlled by this client.
	 */
	public void setPlayer(ClientPlayer player) {
		if (this.player != null)
			throw new IllegalStateException("Player already set!");
		this.player = player;
	}

	/**
	 * Get the world for this client.
	 * @return The world being managed by this client.
	 */
	public World getWorld() { return world; }

	/**
	 * Set the world managed by this client.
	 * @param world The world managed by this client.
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	/** Send a LoginRequest to the server. */
	private void sendLoginRequest() {
		Message loginRequest = ClientMessageGenerator.generateLoginRequest(getUsername(), password);
		setWaitingForLoginResponse(true);
		queueMessage(loginRequest);
	}

	/**
	 * Send a state change request to the server.
	 * @param moveState The move state used to generate the request.
	 */
	protected void sendStateChangeRequest(MoveState moveState) {
		Message stateChangeRequest = ClientMessageGenerator.generateStateChangeRequest(moveState);
		queueMessage(stateChangeRequest);
	}

	@Override
	protected void sendLogoutRequest() {
		Message logoutRequest = new Message("WorldLogoutRequest");

		// Send the request, continue when response is received.
		setWaitingForLogoutResponse(true);
		queueMessage(logoutRequest);
	}
}
