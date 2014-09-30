package com.jenjinstudios.world.client;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.client.message.WorldClientMessageFactory;
import com.jenjinstudios.world.math.SightCalculator;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;

/**
 * Responsible for updating the world.
 * @author Caleb Brinkman
 */
public class WorldClientUpdater implements Runnable
{
	/** The client being updated by this runnable. */
	private final WorldClient worldClient;
	/** The player being controlled by the world client. */
	private final Actor player;

	/**
	 * Construct a new {@code WorldClientUpdater} for the given client.
	 * @param wc The world client.
	 */
	public WorldClientUpdater(WorldClient wc) {
		this.worldClient = wc;
		this.player = worldClient.getPlayer();
	}

	@Override
	public void run() {
		worldClient.getWorld().update();
		SightCalculator.updateVisibleObjects(worldClient.getWorld());
		if (player != null)
		{
			LinkedList<MoveState> newStates = player.getStateChanges();
			while (!newStates.isEmpty())
			{
				MoveState moveState = newStates.remove();
				WorldClientMessageFactory messageFactory = worldClient.getMessageFactory();
				Message stateChangeRequest = messageFactory.generateStateChangeRequest(moveState);
				worldClient.queueOutgoingMessage(stateChangeRequest);
			}
		}
	}
}
