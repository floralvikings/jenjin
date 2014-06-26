package com.jenjinstudios.world;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.io.WorldDocumentException;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.message.WorldClientMessageFactory;
import com.jenjinstudios.world.state.MoveState;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
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
	public static final long TIMEOUT_MILLIS = 30000;
	/** The password used to login to the world. */
	private final String password;
	/** The message factory used to generate messages for this client. */
	private final WorldClientMessageFactory messageFactory;
	/** The world. */
	private World world;
	/** The actor representing the player controlled by this client. */
	private ClientPlayer player;
	/** Whether this client has received a world file checksum from the server. */
	private boolean hasReceivedWorldFileChecksum;
	/** The world file checksum received from the server. */
	private byte[] serverWorldFileChecksum;
	/** The world file. */
	private final File worldFile;
	/** The world file reader for this client. */
	private WorldDocumentReader worldDocumentReader;
	/** Whether this client has received the world file. */
	private boolean hasReceivedWorldFile;
	/** The bytes in the world server file. */
	private byte[] serverWorldFileBytes;

	/**
	 * Construct a client connecting to the given address over the given port.  This client <i>must</i> have a username
	 * and password.
	 * @param username The username that will be used by this client.
	 * @param password The password that will be used by this client.
	 * @param worldFile The file containing the world information.
	 */
	// TODO Wrap these exceptions
	public WorldClient(MessageIO messageIO, String username, String password, File worldFile)
			throws WorldDocumentException
	{
		super(messageIO, username, password);
		this.password = password;
		this.worldFile = worldFile;
		if (worldFile.exists())
		{
			this.worldDocumentReader = new WorldDocumentReader(worldFile);
			this.world = worldDocumentReader.read();
		}
		this.messageFactory = new WorldClientMessageFactory(getMessageRegistry());
	}

	@Override
	public boolean sendBlockingLoginRequest() {
		sendLoginRequest();
		long startTime = System.currentTimeMillis();
		long timePast = System.currentTimeMillis() - startTime;
		while (!isLoggedIn() && isWaitingForLoginResponse() && (timePast < TIMEOUT_MILLIS))
		{
			try
			{
				sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
			timePast = System.currentTimeMillis() - startTime;
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

	/**
	 * Set whether the world file checksum has been received.
	 * @param hasReceivedWorldFileChecksum Whether the checksum has been received.
	 */
	public void setHasReceivedWorldFileChecksum(boolean hasReceivedWorldFileChecksum) {
		this.hasReceivedWorldFileChecksum = hasReceivedWorldFileChecksum;
	}

	/**
	 * Set the checksum received from the server.
	 * @param serverWorldFileChecksum The checksum received from the server.
	 */
	public void setServerWorldFileChecksum(byte[] serverWorldFileChecksum) {
		this.serverWorldFileChecksum = serverWorldFileChecksum;
	}

	/**
	 * Set whether the client has received the world file.
	 * @param hasReceivedWorldFile Whether the client has received the world file.
	 */
	public void setHasReceivedWorldFile(boolean hasReceivedWorldFile) {
		this.hasReceivedWorldFile = hasReceivedWorldFile;
	}

	/**
	 * Set the bytes of the world file stored on the server.
	 * @param serverWorldFileBytes The bytes.
	 */
	public void setServerWorldFileBytes(byte[] serverWorldFileBytes) {
		this.serverWorldFileBytes = serverWorldFileBytes;
	}

	/**
	 * Send a request for the world file, and wait for the response to return.
	 * @throws InterruptedException If the thread is interrupted while waiting for responses.
	 * @throws java.io.IOException If there's an error writing the world file.
	 */
	public void sendBlockingWorldFileRequest() throws InterruptedException, WorldDocumentException, IOException {
		Message worldFileChecksumRequest = getMessageFactory().generateWorldChecksumRequest();
		queueOutgoingMessage(worldFileChecksumRequest);

		while (!hasReceivedWorldFileChecksum)
		{
			Thread.sleep(10);
		}

		if (worldDocumentReader == null || !Arrays.equals(serverWorldFileChecksum, worldDocumentReader.getWorldFileChecksum()))
		{
			queueOutgoingMessage(getMessageFactory().generateWorldFileRequest());
			while (!hasReceivedWorldFile)
			{
				Thread.sleep(10);
			}
			if ((!worldFile.getParentFile().exists() && !worldFile.getParentFile().mkdirs()) || (!worldFile.exists() && !worldFile.createNewFile()))
			{
				throw new IOException("Unable to create new world file!");
			}
			try (FileOutputStream worldOut = new FileOutputStream(worldFile))
			{
				worldOut.write(serverWorldFileBytes);
				worldOut.close();
			}
			worldDocumentReader = new WorldDocumentReader(new ByteArrayInputStream(serverWorldFileBytes));
			world = worldDocumentReader.read();
		}


	}

	/** Send a LoginRequest to the server. */
	private void sendLoginRequest() {
		Message loginRequest = getMessageFactory().generateLoginRequest(getUsername(), password);
		setWaitingForLoginResponse(true);
		queueOutgoingMessage(loginRequest);
	}

	/**
	 * Send a state change request to the server.
	 * @param moveState The move state used to generate the request.
	 */
	protected void sendStateChangeRequest(MoveState moveState) {
		Message stateChangeRequest = getMessageFactory().generateStateChangeRequest(moveState);
		queueOutgoingMessage(stateChangeRequest);
	}

	@Override
	protected void sendLogoutRequest() {
		Message logoutRequest = getMessageFactory().generateWorldLogoutRequest();

		// Send the request, continue when response is received.
		setWaitingForLogoutResponse(true);
		queueOutgoingMessage(logoutRequest);
	}

	public WorldClientMessageFactory getMessageFactory() {return messageFactory; }
}
