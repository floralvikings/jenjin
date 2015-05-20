package com.jenjinstudios.world.client;

import com.jenjinstudios.world.World;

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
		if (world != null)
		{
			world.update();
		}
	}
}
