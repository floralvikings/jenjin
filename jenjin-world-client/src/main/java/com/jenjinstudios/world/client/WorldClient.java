package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.MessageRegistry;
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
public class WorldClient extends AuthClient
{
    private static final Logger LOGGER = Logger.getLogger(WorldClient.class.getName());
    private final WorldClientMessageFactory messageFactory;
    private final ServerWorldFileTracker serverWorldFileTracker;
    private World world;
    private Actor player;

    public WorldClient(MessageIO messageIO, ClientUser clientUser, File worldFile) throws WorldDocumentException {
        super(messageIO, clientUser);
        this.messageFactory = new WorldClientMessageFactory();
        serverWorldFileTracker = new ServerWorldFileTracker(this, worldFile);
        world = serverWorldFileTracker.readWorldFromFile();
		InputStream stream = getClass().getClassLoader().
			  getResourceAsStream("com/jenjinstudios/world/client/Messages.xml");
		MessageRegistry.getInstance().register("World Client/Server Messages", stream);
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
        getServerWorldFileTracker().requestServerWorldFile();
        LOGGER.log(Level.INFO, "Requested World File.");
        getServerWorldFileTracker().waitForWorldFile();
        LOGGER.log(Level.INFO, "Received World File.");
        getServerWorldFileTracker().writeReceivedWorldToFile();
        readWorldFile();
    }

}
