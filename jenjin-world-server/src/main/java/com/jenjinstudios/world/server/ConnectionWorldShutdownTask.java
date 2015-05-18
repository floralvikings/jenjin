package com.jenjinstudios.world.server;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.server.concurrency.ShutdownTask;
import com.jenjinstudios.world.World;

/**
 * Handles removing clients from a world after shutdown.
 *
 * @author Caleb Brinkman
 */
public class ConnectionWorldShutdownTask<T extends WorldServerMessageContext<? extends Player>> implements
	  ShutdownTask<T>
{
	private final World world;

	/**
	 * Construct a new ConnectionWorldShutdownTask that will remove shutdown clients from the specified world.
	 *
	 * @param world The world.
	 */
	public ConnectionWorldShutdownTask(World world) {
		this.world = world;
	}

	@Override
	public void shutdown(Connection<? extends T> connection) {
		Player user = connection.getMessageContext().getUser();
		if (user != null)
		{
			world.getWorldObjects().remove(user.getIdentification().getId());
		}
	}
}
