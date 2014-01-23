package com.jenjinstudios.world;

import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

/** @author Caleb Brinkman */
public class WorldUpdater implements Runnable
{
	/** The client being updated by this runnable. */
	private final WorldClient worldClient;
	/** Actors other than the player. */
	private final TreeMap<Integer, ClientObject> visibleObjects;
	/** The player being controlled by the world client. */
	private final ClientPlayer player;

	public WorldUpdater(WorldClient wc)
	{
		this.worldClient = wc;
		this.player = worldClient.getPlayer();
		this.visibleObjects = player.getVisibleObjects();
	}

	@Override
	public void run() {
		Set<Integer> keys = visibleObjects.keySet();
		for (int i : keys)
		{
			ClientObject currentObject = visibleObjects.get(i);
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
