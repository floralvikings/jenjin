package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.message.WorldClientMessageFactory;
import com.jenjinstudios.world.io.WorldDocumentException;

import java.io.File;
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
	private final ServerWorldFileTracker serverWorldFileTracker;
	private World world;
	private ClientPlayer player;

	public WorldClient(MessageIO messageIO, ClientUser clientUser, File worldFile) throws WorldDocumentException {
		super(messageIO, clientUser);
		this.messageFactory = new WorldClientMessageFactory(getMessageRegistry());
		serverWorldFileTracker = new ServerWorldFileTracker(worldFile);
		world = serverWorldFileTracker.readWorldFromFile();
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

	public ServerWorldFileTracker getServerWorldFileTracker() { return serverWorldFileTracker; }

	public ClientPlayer getPlayer() { return player; }

	public void setPlayer(ClientPlayer player) { this.player = player; }

	public World getWorld() { return world; }

	public void sendBlockingWorldFileRequest() throws InterruptedException, WorldDocumentException {
		serverWorldFileTracker.getServerWorldFileChecksum(this);
		serverWorldFileTracker.readServerWorldFile(this);
		serverWorldFileTracker.writeReceivedWorldToFile();
		world = serverWorldFileTracker.readWorldFromServer();
	}

	private void sendLoginRequest() {
		Message loginRequest = getMessageFactory().generateLoginRequest(getUser());
		setWaitingForLoginResponse(true);
		queueOutgoingMessage(loginRequest);
	}
}
