package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientUpdater;
import com.jenjinstudios.world.math.Vector2D;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles login responses from the server.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginResponse extends WorldClientExecutableMessage
{
    private static final Logger LOGGER = Logger.getLogger(ExecutableWorldLoginResponse.class.getName());

	/**
	 * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
     */
    public ExecutableWorldLoginResponse(WorldClient client, Message message) {
        super(client, message);
    }

    @Override
    public void runDelayed() {
	}

    @Override
    public void runImmediate() {
        int id = (int) getMessage().getArgument("id");
        double xCoordinate = (double) getMessage().getArgument("xCoordinate");
        double yCoordinate = (double) getMessage().getArgument("yCoordinate");
		Actor player = new Actor(getConnection().getUser().getUsername());
		player.addPreUpdateEvent(Vision.EVENT_NAME, new Vision(player));
        player.setId(id);
        Vector2D vector2D = new Vector2D(xCoordinate, yCoordinate);
        player.setVector2D(vector2D);

		WorldClient client = getConnection();
		client.getWorld().scheduleUpdateTask(() -> {
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
		});
	}
}
