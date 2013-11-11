package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.message.Message;
import com.jenjinstudios.net.WorldClient;
import com.jenjinstudios.world.ClientPlayer;

/**
 * Handles login responses from the server.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginResponse extends WorldClientExecutableMessage
{
	/** The player created as indicated by the world login response. */
	private ClientPlayer player;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableWorldLoginResponse(WorldClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runSynced() {
		WorldClient client = getClient();
		client.setReceivedLoginResponse(true);
		client.setLoggedIn((boolean) getMessage().getArgument("success"));

		if (!client.isLoggedIn())
			return;

		client.setLoggedInTime((long) getMessage().getArgument("loginTime"));
		client.setName(client.getUsername());
		client.setPlayer(player);
	}

	@Override
	public void runASync() {
		int id = (int) getMessage().getArgument("id");
		double xCoord = (double) getMessage().getArgument("xCoord");
		double zCoord = (double) getMessage().getArgument("zCoord");
		player = new ClientPlayer(id, getClient().getUsername());
		player.setVector2D(xCoord, zCoord);
	}
}
