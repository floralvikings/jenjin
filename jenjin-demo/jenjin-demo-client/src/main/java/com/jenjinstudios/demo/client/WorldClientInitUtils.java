package com.jenjinstudios.demo.client;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.io.WorldDocumentException;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Caleb Brinkman
 */
public class WorldClientInitUtils
{
	static boolean tryLogin(WorldClient worldClient) {
		boolean success = false;
		try
		{
			loginAndWaitForResponse(worldClient);
			success = true;
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return success;
	}

	static boolean tryRequestWorldFile(WorldClient worldClient) {
		boolean success = false;
		try
		{
			requestWorldFile(worldClient);
			success = true;
		} catch (InterruptedException | WorldDocumentException e)
		{
			e.printStackTrace();
		}
		return success;
	}

	static WorldClient tryCreateWorldClient(String address, int port, String username, String password) {
		WorldClient worldClient;
		try
		{
			worldClient = createWorldClient(address, port, username, password);
		} catch (IOException | WorldDocumentException e)
		{
			e.printStackTrace();
			worldClient = null;
		}
		return worldClient;
	}

	private static void loginAndWaitForResponse(WorldClient worldClient) throws InterruptedException {
		sendLoginRequest(worldClient);
		long start = System.currentTimeMillis();
		while (!worldClient.isLoggedIn() && System.currentTimeMillis() - start < 30000)
		{
			Thread.sleep(10);
		}
	}

	private static void requestWorldFile(WorldClient worldClient) throws InterruptedException, WorldDocumentException {
		worldClient.getServerWorldFileTracker().requestServerWorldFileChecksum(worldClient);
		worldClient.getServerWorldFileTracker().requestServerWorldFile(worldClient);
		worldClient.getServerWorldFileTracker().writeReceivedWorldToFile();
	}

	private static WorldClient createWorldClient(String address, int port, String username, String password)
		  throws IOException, WorldDocumentException
	{
		ClientUser clientUser = new ClientUser(username, password);
		File worldFile = new File(System.getProperty("user.home") + "/jenjin/World.xml");
		Socket socket = new Socket(address, port);
		MessageInputStream messageInputStream = new MessageInputStream(socket.getInputStream());
		MessageOutputStream messageOutputStream = new MessageOutputStream(socket.getOutputStream());
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream);
		return new WorldClient(messageIO, clientUser, worldFile);
	}

	private static void sendLoginRequest(WorldClient worldClient) {
		Message message = worldClient.getMessageFactory().generateLoginRequest(worldClient.getUser());
		worldClient.queueOutgoingMessage(message);
	}
}
