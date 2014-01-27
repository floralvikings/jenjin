package com.jenjinstudios.world;

import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Responsible for updating the world.
 * @author Caleb Brinkman
 */
public class WorldClientUpdater implements Runnable
{
	/** The client being updated by this runnable. */
	private final WorldClient worldClient;
	/** Actors other than the player. */
	private final TreeMap<Integer, WorldObject> visibleObjects;
	/** The player being controlled by the world client. */
	private final ClientPlayer player;

	/**
	 * Construct a new {@code WorldClientUpdater} for the given client.
	 * @param wc The world client.
	 */
	public WorldClientUpdater(WorldClient wc) {
		this.worldClient = wc;
		this.player = worldClient.getPlayer();
		this.visibleObjects = player.getVisibleObjects();
	}

	@Override
	public void run() {
		for (WorldObject currentObject : visibleObjects.values())
		{
			currentObject.update();
		}
		if (player != null)
		{
			player.update();
			LinkedList<MoveState> newStates = player.getSavedStates();
			while (!newStates.isEmpty())
				worldClient.sendStateChangeRequest(newStates.remove());
		}
	}
}
