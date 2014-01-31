package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.ClientPlayer;
import com.jenjinstudios.world.InvalidLocationException;
import com.jenjinstudios.world.WorldClient;
import com.jenjinstudios.world.WorldClientUpdater;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles login responses from the server.
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginResponse extends WorldClientExecutableMessage
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutableWorldFileResponse.class.getName());
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
		client.setWaitingForLoginResponse(false);
		client.setLoggedIn((boolean) getMessage().getArgument("success"));

		if (!client.isLoggedIn())
			return;

		client.setLoggedInTime((long) getMessage().getArgument("loginTime"));
		client.setName(client.getUsername());
		client.setPlayer(player);
		try
		{
			client.getWorld().addObject(player, player.getId());
		} catch (InvalidLocationException e)
		{
			LOGGER.log(Level.INFO, "Tried to place newly visible actor in invalid location.");
		}

		client.addRepeatedTask(new WorldClientUpdater(client));
	}

	@Override
	public void runASync() {
		int id = (int) getMessage().getArgument("id");
		double xCoord = (double) getMessage().getArgument("xCoordinate");
		double yCoord = (double) getMessage().getArgument("yCoordinate");
		player = new ClientPlayer(id, getClient().getUsername());
		player.setVector2D(xCoord, yCoord);
	}
}
