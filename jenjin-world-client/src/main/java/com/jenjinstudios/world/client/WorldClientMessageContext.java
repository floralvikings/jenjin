package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.ClientMessageContext;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.object.Actor;

/**
 * Represents the context in which world client messages sould execute.
 *
 * @author Caleb Brinkman
 */
public class WorldClientMessageContext extends ClientMessageContext
{
	private final WorldFileTracker worldFileTracker = new WorldFileTracker();
	private Actor player;
	private World world;

	/**
	 * Get the player managed by this context.
	 *
	 * @return The player.
	 */
	public Actor getPlayer() { return player; }

	/**
	 * Set the player managed by this context.
	 *
	 * @param player The player.
	 */
	public void setPlayer(Actor player) { this.player = player; }

	/**
	 * Get the world managed by this context.
	 *
	 * @return The world managed by this context.
	 */
	public World getWorld() { return world; }

	/**
	 * Set the world managed by this context.
	 *
	 * @param world The world to be managed by this context.
	 */
	public void setWorld(World world) { this.world = world; }

	/**
	 * Get the WorldFileTracker managed by this context.
	 *
	 * @return The WorldFileTracker managed by this context.
	 */
	public WorldFileTracker getWorldFileTracker() { return worldFileTracker; }
}
