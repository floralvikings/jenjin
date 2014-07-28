package com.jenjinstudios.demo.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.demo.server.Bullet;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.message.WorldExecutableMessage;

/**
 * @author Caleb Brinkman
 */
public class ExecutableFireRequest extends WorldExecutableMessage
{
	private Bullet bullet;

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableFireRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runDelayed() {
		World world = ((WorldServer) getClientHandler().getServer()).getWorld();
		if (world.getZone(bullet.getZoneID()).getLocationForCoordinates(bullet.getVector2D()) != null)
		{
			world.addObject(bullet);
		}
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runImmediate() {
		Player player = getClientHandler().getPlayer();
		bullet = new Bullet(player);
		bullet.setZoneID(player.getZoneID());
	}
}
