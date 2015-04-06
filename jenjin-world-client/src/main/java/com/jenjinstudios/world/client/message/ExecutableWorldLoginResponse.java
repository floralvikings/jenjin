package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.math.Vector2D;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles login responses from the server.
 *
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginResponse extends WorldClientExecutableMessage<WorldClientMessageContext>
{
    private static final Logger LOGGER = Logger.getLogger(ExecutableWorldLoginResponse.class.getName());

	/**
	 * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableWorldLoginResponse(WorldClient client, Message message, WorldClientMessageContext context) {
		super(client, message, context);
	}

    @Override
	public Message execute() {
		int id = (int) getMessage().getArgument("id");
        double xCoordinate = (double) getMessage().getArgument("xCoordinate");
        double yCoordinate = (double) getMessage().getArgument("yCoordinate");
		Actor player = new Actor(getContext().getUser().getUsername());
		player.addPreUpdateEvent(Vision.EVENT_NAME, new Vision(player));
        player.setId(id);
        Vector2D vector2D = new Vector2D(xCoordinate, yCoordinate);
        player.setVector2D(vector2D);

		WorldClient client = getWorldClient();
		getContext().getWorld().scheduleUpdateTask(() -> {
			boolean success = (boolean) getMessage().getArgument("success");
			getContext().getLoginTracker().setLoggedIn(success);
			if (success)
			{
				LOGGER.log(Level.INFO, "Logged in successfully; Player ID: " + player.getId());
				getContext().getLoginTracker().setLoggedInTime((long) getMessage().getArgument("loginTime"));
				getContext().setName(getContext().getUser().getUsername());
				getContext().setPlayer(player);
				getContext().getWorld().getWorldObjects().set(player.getId(), player);
			}
		});
		return null;
	}
}
