package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.ClientPlayer;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientUpdater;
import com.jenjinstudios.world.math.Vector2D;

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
	public void runDelayed() {
		WorldClient client = getClient();
		boolean success = (boolean) getMessage().getArgument("success");
		client.getLoginTracker().setLoggedIn(success);
		if (success)
		{

			client.getLoginTracker().setLoggedInTime((long) getMessage().getArgument("loginTime"));
			client.setName(client.getUser().getUsername());
			client.setPlayer(player);
			client.getWorld().addObject(player, player.getId());

			client.addRepeatedTask(new WorldClientUpdater(client));
		}
	}

	@Override
	public void runImmediate() {
		int id = (int) getMessage().getArgument("id");
		double xCoordinate = (double) getMessage().getArgument("xCoordinate");
		double yCoordinate = (double) getMessage().getArgument("yCoordinate");
		player = new ClientPlayer(id, getClient().getUser().getUsername());
		Vector2D vector2D = new Vector2D(xCoordinate, yCoordinate);
		player.setVector2D(vector2D);
	}
}
