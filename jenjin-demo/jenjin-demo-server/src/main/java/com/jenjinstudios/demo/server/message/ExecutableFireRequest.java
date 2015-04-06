package com.jenjinstudios.demo.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.demo.server.Bullet;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.server.WorldServerMessageContext;
import com.jenjinstudios.world.server.message.WorldExecutableMessage;

/**
 * @author Caleb Brinkman
 */
public class ExecutableFireRequest extends WorldExecutableMessage<WorldServerMessageContext>
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param message The message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableFireRequest(Message message, WorldServerMessageContext context)
	{
		super(message, context);
	}

	/** Run asynchronous portion of this message. */
	@Override
	public Message execute() {
		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			Actor player = getContext().getUser();
			Bullet bullet = new Bullet(player);
			world.getWorldObjects().add(bullet);
		});
		return null;
	}
}
