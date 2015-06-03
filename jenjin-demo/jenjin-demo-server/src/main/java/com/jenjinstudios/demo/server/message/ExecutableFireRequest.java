package com.jenjinstudios.demo.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.demo.server.Bullet;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldServerMessageContext;
import com.jenjinstudios.world.server.message.WorldExecutableMessage;

/**
 * Request executed when a client requests to fire a bullet.
 *
 * @author Caleb Brinkman
 */
public class ExecutableFireRequest extends WorldExecutableMessage<WorldServerMessageContext<Player>>
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param message The message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableFireRequest(Message message, WorldServerMessageContext<Player> context)
	{
		super(message, context);
	}

	/** Run asynchronous portion of this message. */
	@Override
	public Message execute() {
		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			Player player = getContext().getUser();
			Bullet bullet = new Bullet(player);
			player.getParent().addChild(bullet);
		});
		return null;
	}
}
