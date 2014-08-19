package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
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
	private final WorldClientMessageFactory messageFactory;
	private final ServerWorldFileTracker serverWorldFileTracker;
	private World world;
	private ClientPlayer player;

	public WorldClient(MessageIO messageIO, ClientUser clientUser, File worldFile) throws WorldDocumentException {
		super(messageIO, clientUser);
		this.messageFactory = new WorldClientMessageFactory();
		serverWorldFileTracker = new ServerWorldFileTracker(this, worldFile);
		world = serverWorldFileTracker.readWorldFromFile();
	}

	@Override
	public WorldClientMessageFactory getMessageFactory() {return messageFactory; }

	public ServerWorldFileTracker getServerWorldFileTracker() { return serverWorldFileTracker; }

	public ClientPlayer getPlayer() { return player; }

	public void setPlayer(ClientPlayer player) { this.player = player; }

	public World getWorld() { return world; }

	public void readWorldFile() throws WorldDocumentException { world = serverWorldFileTracker.readWorldFromFile(); }

	public void startAndInitialize() {
		start();
		while (!isInitialized())
		{
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.FINEST, "Interrupted during sleep cycle while waiting for initialization.");
			}
		}
	}

}
