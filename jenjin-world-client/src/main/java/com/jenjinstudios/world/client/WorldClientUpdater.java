package com.jenjinstudios.world.client;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.message.WorldClientMessageFactory;
import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.state.MoveState;

import java.util.List;

/**
 * Responsible for updating the world.
 *
 * @author Caleb Brinkman
 */
public class WorldClientUpdater implements Runnable
{
	/** The client being updated by this runnable. */
	private final WorldClient worldClient;

	/**
	 * Construct a new {@code WorldClientUpdater} for the given client.
	 *
	 * @param wc The world client.
	 */
	public WorldClientUpdater(WorldClient wc) {
		this.worldClient = wc;
	}

	@Override
	public void run() {
		World world = worldClient.getWorld();
		Actor player = worldClient.getPlayer();
		if (world != null)
		{
			world.update();
		}
		if (player != null)
		{
			List<MoveState> newStates = player.getStateChanges();
			for (MoveState moveState : newStates) {
				WorldClientMessageFactory messageFactory = worldClient.getMessageFactory();
				Message stateChangeRequest = messageFactory.generateStateChangeRequest(moveState);
				worldClient.enqueueMessage(stateChangeRequest);
			}
		}
	}
}
