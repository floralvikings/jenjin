package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.message.WorldClientMessageFactory;
import com.jenjinstudios.world.io.WorldDocumentException;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.state.MoveState;

import java.io.*;
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
	private static final long TIMEOUT_MILLIS = 30000;
	/** The message factory used to generate messages for this client. */
	private final WorldClientMessageFactory messageFactory;
	/** The world file. */
	private final File worldFile;
	/** The world. */
	private World world;
	/** The actor representing the player controlled by this client. */
	private ClientPlayer player;
	/** Whether this client has received a world file checksum from the server. */
	private boolean hasReceivedWorldFileChecksum;
	/** The world file checksum received from the server. */
	private byte[] serverWorldFileChecksum;
	/** The world file reader for this client. */
	private WorldDocumentReader worldDocumentReader;
	/** Whether this client has received the world file. */
	private boolean hasReceivedWorldFile;
	/** The bytes in the world server file. */
	private byte[] serverWorldFileBytes;

	/**
	 * Construct a WorldClient.
	 * @param worldFile The file containing the world information.
	 */
	public WorldClient(MessageIO messageIO, ClientUser clientUser, File worldFile) throws WorldDocumentException {
		super(messageIO, clientUser);
		this.worldFile = worldFile;
		if (worldFile.exists())
		{
			FileInputStream fileInputStream = getWorldFileInputStream(worldFile);
			this.worldDocumentReader = new WorldDocumentReader(fileInputStream);
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

	@Override
	protected void sendLogoutRequest() {
		Message logoutRequest = getMessageFactory().generateWorldLogoutRequest();

		// Send the request, continue when response is received.
		setWaitingForLogoutResponse(true);
		queueOutgoingMessage(logoutRequest);
	}

	@Override
	public WorldClientMessageFactory getMessageFactory() {return messageFactory; }

	public ClientPlayer getPlayer() { return player; }

	public void setPlayer(ClientPlayer player) {
		this.player = player;
	}

	public World getWorld() { return world; }

	public void setHasReceivedWorldFileChecksum() {
		this.hasReceivedWorldFileChecksum = true;
	}

	public void setServerWorldFileChecksum(byte[] serverWorldFileChecksum) {
		this.serverWorldFileChecksum = serverWorldFileChecksum;
	}

	public void setHasReceivedWorldFile() {
		this.hasReceivedWorldFile = true;
	}

	public void setServerWorldFileBytes(byte[] serverWorldFileBytes) {
		this.serverWorldFileBytes = serverWorldFileBytes;
	}

	public void sendBlockingWorldFileRequest() throws InterruptedException, WorldDocumentException {
		Message worldFileChecksumRequest = getMessageFactory().generateWorldChecksumRequest();
		queueOutgoingMessage(worldFileChecksumRequest);

		waitForWorldFileChecksum();

		if (needsWorldFile())
		{
			queueOutgoingMessage(getMessageFactory().generateWorldFileRequest());
			waitForWorldFile();
			createNewFileIfNecessary();
			writeServerWorldToFile();
			readServerWorldFromFile();
		}


	}

	protected void sendStateChangeRequest(MoveState moveState) {
		Message stateChangeRequest = getMessageFactory().generateStateChangeRequest(moveState);
		queueOutgoingMessage(stateChangeRequest);
	}

	private void createNewFileIfNecessary() throws WorldDocumentException {
		if (!tryCreateWorldFileDirectory() || !tryCreateWorldFile())
		{
			throw new WorldDocumentException("Unable to create new world file!");
		}
	}

	private boolean tryCreateWorldFile() throws WorldDocumentException {
		try
		{
			return worldFile.exists() || worldFile.createNewFile();
		} catch (IOException e)
		{
			throw new WorldDocumentException("Unable to create new file.", e);
		}
	}

	private boolean tryCreateWorldFileDirectory() {
		return worldFile.getParentFile().exists() || worldFile.getParentFile().mkdirs();
	}

	private boolean needsWorldFile() {
		return worldDocumentReader == null || !Arrays.equals(serverWorldFileChecksum,
			  worldDocumentReader.getWorldFileChecksum());
	}

	private void writeServerWorldToFile() throws WorldDocumentException {
		try (FileOutputStream worldOut = new FileOutputStream(worldFile))
		{
			worldOut.write(serverWorldFileBytes);
			worldOut.close();
		} catch (IOException ex)
		{
			throw new WorldDocumentException("Unable to write world file.", ex);
		}
	}

	private void readServerWorldFromFile() throws WorldDocumentException {
		worldDocumentReader = new WorldDocumentReader(new ByteArrayInputStream(serverWorldFileBytes));
		world = worldDocumentReader.read();
	}

	private void waitForWorldFile() throws InterruptedException {
		while (!hasReceivedWorldFile)
		{
			Thread.sleep(10);
		}
	}

	private void waitForWorldFileChecksum() throws InterruptedException {
		while (!hasReceivedWorldFileChecksum)
		{
			Thread.sleep(10);
		}
	}

	private FileInputStream getWorldFileInputStream(File worldFile) throws WorldDocumentException {
		FileInputStream fileInputStream;
		try
		{
			fileInputStream = new FileInputStream(worldFile);
		} catch (FileNotFoundException e)
		{
			throw new WorldDocumentException("Unable to find world file.", e);
		}
		return fileInputStream;
	}

	private void sendLoginRequest() {
		Message loginRequest = getMessageFactory().generateLoginRequest(getUser());
		setWaitingForLoginResponse(true);
		queueOutgoingMessage(loginRequest);
	}
}
