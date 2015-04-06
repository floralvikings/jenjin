package com.jenjinstudios.world.server;

import com.jenjinstudios.server.net.ServerMessageContext;
import com.jenjinstudios.world.World;

/**
 * Used to maintain context for messages executed on a WorldServer connection.
 *
 * @author Caleb Brinkman
 */
public class WorldServerMessageContext<T extends Player> extends ServerMessageContext<T>
{
	private World world;

	/**
	 * Get the world managed by this context.
	 *
	 * @return The world.
	 */
	public World getWorld() { return world; }

	/**
	 * Set the world managed by this context.
	 *
	 * @param world The world.
	 */
	public void setWorld(World world) { this.world = world; }
}
