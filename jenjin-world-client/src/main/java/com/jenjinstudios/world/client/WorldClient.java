package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.Client;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.message.WorldClientMessageFactory;
import com.jenjinstudios.world.io.WorldDocumentException;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The WorldClient class is used to connect to a WorldServer and stores information about the environment immediately
 * surrounding the player.
 *
 * @author Caleb Brinkman
 */
public class WorldClient<T extends WorldClientMessageContext> extends Client<T>
{
    private static final Logger LOGGER = Logger.getLogger(WorldClient.class.getName());
    private final WorldClientMessageFactory messageFactory;
    private final WorldFileTracker worldFileTracker;
	private final File worldFile;
	private World world;

	public WorldClient(MessageStreamPair messageStreamPair, File worldFile, T context) throws
		  WorldDocumentException {
		super(messageStreamPair, context);
		this.worldFile = worldFile;
		this.messageFactory = new WorldClientMessageFactory();
		worldFileTracker = new WorldFileTracker(worldFile);
		world = WorldFileTracker.readWorldFromFile(worldFileTracker, worldFile);
		getMessageContext().setWorld(world);
		InputStream stream = getClass().getClassLoader().
			  getResourceAsStream("com/jenjinstudios/world/client/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("World Client/Server Messages", stream);
	}

	public static void requestChecksum(WorldClient worldClient) {
		Message checksumRequest = worldClient.getMessageFactory().generateWorldChecksumRequest();
		worldClient.enqueueMessage(checksumRequest);
	}

	public void waitForCheckSum() {
		while (worldFileTracker.isWaitingForChecksum())
		{
			waitTenMillis();
		}
	}

	static void waitTenMillis() {
		try
		{
			Thread.sleep(10);
		} catch (InterruptedException e)
		{
			LOGGER.log(Level.WARNING, "Interrupted while waiting.");
		}
	}

	public WorldClientMessageFactory getMessageFactory() {return messageFactory; }

    public WorldFileTracker getWorldFileTracker() { return worldFileTracker; }

	public Actor getPlayer() { return getMessageContext().getPlayer(); }

	public World getWorld() { return world; }

	public void readWorldFile() throws WorldDocumentException {
		world = WorldFileTracker.readWorldFromFile(worldFileTracker, worldFile);
	}

    public void initializeWorldFromServer() throws WorldDocumentException {
        getWorldFileTracker().setWaitingForChecksum(true);
		requestChecksum(this);
		LOGGER.log(Level.INFO, "Requested World Checksum.");
		waitForCheckSum();
		LOGGER.log(Level.INFO, "Received World Checksum.");
		if (worldFileTracker.needsWorldFile())
		{
			worldFileTracker.setWaitingForFile(true);
			enqueueMessage(messageFactory.generateWorldFileRequest());
			LOGGER.log(Level.INFO, "Requested World File.");
			getWorldFileTracker().waitForWorldFile();
			LOGGER.log(Level.INFO, "Received World File.");
			getWorldFileTracker().writeReceivedWorldToFile();
		}
		readWorldFile();
    }

}
