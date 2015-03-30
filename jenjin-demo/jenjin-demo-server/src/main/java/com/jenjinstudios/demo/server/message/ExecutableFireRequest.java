package com.jenjinstudios.demo.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.demo.server.Bullet;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.message.WorldExecutableMessage;

/**
 * @author Caleb Brinkman
 */
public class ExecutableFireRequest extends WorldExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutableFireRequest(WorldClientHandler handler, Message message) {
		super(handler, message);
	}

	/** Run asynchronous portion of this message. */
	@Override
	public Message execute() {
		World world = ((WorldServer) getClientHandler().getServer()).getWorld();
		world.scheduleUpdateTask(() -> {
			Actor player = getClientHandler().getUser();
			Bullet bullet = new Bullet(player);
			world.getWorldObjects().add(bullet);
		});
		return null;
	}
}
