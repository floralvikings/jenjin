package com.jenjinstudios.demo.client;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.io.WorldDocumentException;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public class WorldClientInitUtils
{

	private static final Logger LOGGER = Logger.getLogger(WorldClientInitUtils.class.getName());

	public static boolean tryRequestWorldFile(WorldClient worldClient) {
		boolean success = false;
		try
		{
			requestWorldFile(worldClient);
			success = true;
		} catch (WorldDocumentException e)
		{
			LOGGER.log(Level.SEVERE, "Exception downloading world file.", e);
		}
		return success;
	}

	public static WorldClient tryCreateWorldClient(String address, int port, ClientUser user) {
		WorldClient worldClient;
		try
		{
			worldClient = createWorldClient(address, port, user);
		} catch (IOException | WorldDocumentException e)
		{
			LOGGER.log(Level.SEVERE, "Exception creating world client.", e);
			worldClient = null;
		}
		return worldClient;
	}

	private static void requestWorldFile(WorldClient worldClient) throws WorldDocumentException {
		worldClient.getServerWorldFileTracker().requestServerWorldFileChecksum();
		worldClient.getServerWorldFileTracker().requestServerWorldFile();
		worldClient.getServerWorldFileTracker().writeReceivedWorldToFile();
	}

	private static WorldClient createWorldClient(String address, int port, ClientUser clientUser)
		  throws IOException, WorldDocumentException
	{
		File worldFile = new File(System.getProperty("user.home") + "/.jenjin-demo/World.xml");
		Socket socket = new Socket(address, port);
		MessageInputStream messageInputStream = new MessageInputStream(socket.getInputStream());
		MessageOutputStream messageOutputStream = new MessageOutputStream(socket.getOutputStream());
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream);
		return new WorldClient(messageIO, clientUser, worldFile);
	}

}
