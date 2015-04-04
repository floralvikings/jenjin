package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.Client;
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
    private final ServerWorldFileTracker serverWorldFileTracker;
    private World world;
    private Actor player;

	public WorldClient(MessageStreamPair messageStreamPair, File worldFile, T context) throws
		  WorldDocumentException {
		super(messageStreamPair, context);
		this.messageFactory = new WorldClientMessageFactory();
        serverWorldFileTracker = new ServerWorldFileTracker(this, worldFile);
        world = serverWorldFileTracker.readWorldFromFile();
		getMessageContext().setWorld(world);
		InputStream stream = getClass().getClassLoader().
			  getResourceAsStream("com/jenjinstudios/world/client/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("World Client/Server Messages", stream);
	}

	public static void requestWorldFile(ServerWorldFileTracker serverWorldFileTracker, WorldClient worldClient) {
		if (serverWorldFileTracker.needsWorldFile())
		{
			worldClient.enqueueMessage(worldClient.getMessageFactory().generateWorldFileRequest());
		} else
		{
			serverWorldFileTracker.setWaitingForFile(false);
		}
	}

	public WorldClientMessageFactory getMessageFactory() {return messageFactory; }

    public ServerWorldFileTracker getServerWorldFileTracker() { return serverWorldFileTracker; }

    public Actor getPlayer() { return player; }

    public void setPlayer(Actor player) { this.player = player; }

    public World getWorld() { return world; }

    public void readWorldFile() throws WorldDocumentException { world = serverWorldFileTracker.readWorldFromFile(); }

    public void initializeWorldFromServer() throws WorldDocumentException {
        getServerWorldFileTracker().setWaitingForChecksum(true);
        getServerWorldFileTracker().requestServerWorldFileChecksum();
        LOGGER.log(Level.INFO, "Requested World Checksum.");
        getServerWorldFileTracker().waitForWorldFileChecksum();
        LOGGER.log(Level.INFO, "Received World Checksum.");
        getServerWorldFileTracker().setWaitingForFile(true);
		requestWorldFile(getServerWorldFileTracker(), this);
		LOGGER.log(Level.INFO, "Requested World File.");
        getServerWorldFileTracker().waitForWorldFile();
        LOGGER.log(Level.INFO, "Received World File.");
        getServerWorldFileTracker().writeReceivedWorldToFile();
        readWorldFile();
    }

}
