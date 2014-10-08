package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientUpdater;
import com.jenjinstudios.world.math.Vector2D;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles login responses from the server.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginResponse extends WorldClientExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableWorldLoginResponse.class.getName());
	/** The player created as indicated by the world login response. */
	private Actor player;

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
			LOGGER.log(Level.INFO, "Logged in successfully; Player ID: " + player.getId());
			client.getLoginTracker().setLoggedInTime((long) getMessage().getArgument("loginTime"));
			client.setName(client.getUser().getUsername());
			client.setPlayer(player);
			client.getWorld().getWorldObjects().set(player.getId(), player);

			client.addRepeatedTask(new WorldClientUpdater(client));
		}
	}

	@Override
	public void runImmediate() {
		int id = (int) getMessage().getArgument("id");
		double xCoordinate = (double) getMessage().getArgument("xCoordinate");
		double yCoordinate = (double) getMessage().getArgument("yCoordinate");
		player = new Actor(getClient().getUser().getUsername());
		player.setId(id);
		Vector2D vector2D = new Vector2D(xCoordinate, yCoordinate);
		player.setVector2D(vector2D);
	}
}
