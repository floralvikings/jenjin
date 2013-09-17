package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.WorldClient;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.client.ClientActor;

/**
 * Handles login responses from the server.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginResponse extends WorldClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param client  The client invoking this message.
	 * @param message The Message.
	 */
	protected ExecutableWorldLoginResponse(WorldClient client, Message message)
	{
		super(client, message);
	}

	@Override
	public void runSynced()
	{
		WorldClient client = getClient();
		client.setReceivedLoginResponse(true);
		client.setLoggedIn((boolean) getMessage().getArgument("success"));
		if (!client.isLoggedIn())
			return;
		client.setLoggedInTime((long) getMessage().getArgument("loginTime"));
		client.setName(client.getUsername());

		double xCoord = (double) getMessage().getArgument("xCoord");
		double zCoord = (double) getMessage().getArgument("zCoord");
		ClientActor player = new ClientActor(client.getUsername());
		player.setVector2D(xCoord, zCoord);

		client.setPlayer(player);
	}

	@Override
	public void runASync()
	{
	}
}
