package com.jenjinstudios.world.client;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.actor.StateChangeStack;
import com.jenjinstudios.world.client.message.WorldClientMessageFactory;
import com.jenjinstudios.world.event.EventStack;
import com.jenjinstudios.world.state.MoveState;

import java.util.List;

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
		if (player != null)
		{
			EventStack eventStack = player.getEventStack(StateChangeStack.STACK_NAME);
			if (eventStack != null && eventStack instanceof StateChangeStack)
			{
				StateChangeStack stateChangeStack = (StateChangeStack) eventStack;
				List<MoveState> newStates = stateChangeStack.getStateChanges();
				while (!newStates.isEmpty())
				{
					MoveState moveState = newStates.remove(0);
					WorldClientMessageFactory messageFactory = worldClient.getMessageFactory();
					Message stateChangeRequest = messageFactory.generateStateChangeRequest(moveState);
					worldClient.queueOutgoingMessage(stateChangeRequest);
				}
			}
		}
	}
}
