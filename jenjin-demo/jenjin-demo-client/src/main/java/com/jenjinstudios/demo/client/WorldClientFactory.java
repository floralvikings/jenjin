package com.jenjinstudios.demo.client;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.world.client.WorldClient;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public class WorldClientFactory
{

	private static final Logger LOGGER = Logger.getLogger(WorldClientFactory.class.getName());

	public static WorldClient tryCreateWorldClient(String address, int port, ClientUser user) {
		WorldClient worldClient;
		try
		{
			worldClient = createWorldClient(address, port, user);
			worldClient.start();
			worldClient.getServerWorldFileTracker().setWaitingForChecksum(true);
			worldClient.getServerWorldFileTracker().requestServerWorldFileChecksum();
			LOGGER.log(Level.INFO, "Requested World Checksum.");
			worldClient.getServerWorldFileTracker().waitForWorldFileChecksum();
			LOGGER.log(Level.INFO, "Received World Checksum.");
			worldClient.getServerWorldFileTracker().setWaitingForFile(true);
			worldClient.getServerWorldFileTracker().requestServerWorldFile();
			LOGGER.log(Level.INFO, "Requested World File.");
			worldClient.getServerWorldFileTracker().waitForWorldFile();
			LOGGER.log(Level.INFO, "Received World File.");
			worldClient.getServerWorldFileTracker().writeReceivedWorldToFile();
			worldClient.readWorldFile();
		} catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Exception creating world client.", e);
			worldClient = null;
		}
		LOGGER.log(Level.INFO, "Created World Client.");
		return worldClient;
	}

	private static WorldClient createWorldClient(String address, int port, ClientUser clientUser) throws IOException {
		File worldFile = new File(System.getProperty("user.home") + "/.jenjin-demo/World.xml");
		Socket socket = new Socket(address, port);
		MessageInputStream messageInputStream = new MessageInputStream(socket.getInputStream());
		MessageOutputStream messageOutputStream = new MessageOutputStream(socket.getOutputStream());
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream);
		return new WorldClient(messageIO, clientUser, worldFile);
	}

}
