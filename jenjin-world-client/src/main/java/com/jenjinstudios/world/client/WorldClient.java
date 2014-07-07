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
	private static final Logger LOGGER = Logger.getLogger(WorldClient.class.getName());
	private static final long TIMEOUT_MILLIS = 30000;
	private final WorldClientMessageFactory messageFactory;
	private final File worldFile;
	private World world;
	private ClientPlayer player;
	private WorldDocumentReader worldDocumentReader;
	private final ServerWorldFileTracker serverWorldFileTracker;

	public WorldClient(MessageIO messageIO, ClientUser clientUser, File worldFile) throws WorldDocumentException {
		super(messageIO, clientUser);
		serverWorldFileTracker = new ServerWorldFileTracker();
		this.worldFile = worldFile;
		if (worldFile.exists())
		{
			readWorldFile();
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

	public ServerWorldFileTracker getServerWorldFileTracker() {
		return serverWorldFileTracker;
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
			readWorldFile();
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
		return worldDocumentReader == null || !Arrays.equals(serverWorldFileTracker.getChecksum(),
			  worldDocumentReader.getWorldFileChecksum());
	}

	private void writeServerWorldToFile() throws WorldDocumentException {
		try (FileOutputStream worldOut = new FileOutputStream(worldFile))
		{
			worldOut.write(serverWorldFileTracker.getBytes());
			worldOut.close();
		} catch (IOException ex)
		{
			throw new WorldDocumentException("Unable to write world file.", ex);
		}
	}

	private void readWorldFile() throws WorldDocumentException {
		try
		{
			FileInputStream inputStream = new FileInputStream(worldFile);
			worldDocumentReader = new WorldDocumentReader(inputStream);
			world = worldDocumentReader.read();
		} catch (FileNotFoundException e)
		{
			throw new WorldDocumentException("Couldn't find world file.", e);
		}
	}

	private void waitForWorldFile() throws InterruptedException {
		serverWorldFileTracker.setWaitingForFile(true);
		while (serverWorldFileTracker.isWaitingForFile())
		{
			Thread.sleep(10);
		}
	}

	private void waitForWorldFileChecksum() throws InterruptedException {
		serverWorldFileTracker.setWaitingForChecksum(true);
		while (serverWorldFileTracker.isWaitingForChecksum())
		{
			Thread.sleep(10);
		}
	}

	private void sendLoginRequest() {
		Message loginRequest = getMessageFactory().generateLoginRequest(getUser());
		setWaitingForLoginResponse(true);
		queueOutgoingMessage(loginRequest);
	}
}
