package com.jenjinstudios.world.server;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.concurrency.UpdateTask;
import com.jenjinstudios.world.server.message.WorldServerMessageFactory;

/**
 * Task to update WorldClientHandlers at intervals.
 *
 * @author Caleb Brinkman
 */
public class ConnectionWorldUpdateTask<T extends WorldServerMessageContext<? extends Player>> implements UpdateTask<T>
{
	@Override
	public void update(Connection<? extends T> connection) {
		WorldServerMessageContext<? extends Player> context = connection.getMessageContext();
		if (context.getUser() != null)
		{
			if (context.needsToSendSpeedMessage())
			{
				double moveSpeed = context.getUser().getGeometry().getSpeed();
				Message message = WorldServerMessageFactory.generateActorMoveSpeedMessage(moveSpeed);
				context.enqueue(message);
				context.setNeedsToSendSpeedMessage(false);
			}
			queueForcesStateMessage(connection);
		}
	}

	private void queueForcesStateMessage(Connection<? extends T> connection) {
		// TODO Use player angle to determine if state is forced
	}
}
