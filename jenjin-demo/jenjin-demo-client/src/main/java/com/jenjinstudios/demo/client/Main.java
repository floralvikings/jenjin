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
public class Main
{
	public static void main(String[] args) throws Exception {
		WorldClient worldClient = createWorldClient(args);
		worldClient.start();
		requestWorldFile(worldClient);
		sendLoginRequest(worldClient);
		loginAndWaitForResponse(worldClient);
		if (worldClient.isLoggedIn())
		{
			System.out.println("Successfully logged in!");
		} else
		{
			System.out.println("Login unsuccessful");
			worldClient.shutdown();
		}
	}

	private static void loginAndWaitForResponse(WorldClient worldClient) throws InterruptedException {
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

	private static WorldClient createWorldClient(String[] args) throws IOException, WorldDocumentException {
		String address = args[0];
		int port = Integer.parseInt(args[1]);
		String userName = args[2];
		String password = args[3];
		ClientUser clientUser = new ClientUser(userName, password);
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
