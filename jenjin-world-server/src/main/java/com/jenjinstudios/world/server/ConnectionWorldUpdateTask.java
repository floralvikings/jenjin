package com.jenjinstudios.world.server;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.concurrency.UpdateTask;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.server.message.WorldServerMessageFactory;
import com.jenjinstudios.world.state.MoveState;

import java.util.List;
import java.util.Set;

/**
 * Task to update WorldClientHandlers at intervals.
 *
 * @author Caleb Brinkman
 */
public class ConnectionWorldUpdateTask<T extends WorldServerMessageContext> implements UpdateTask<T>
{
	@Override
	public void update(Connection<? extends T> connection) {
		WorldServerMessageContext context = connection.getMessageContext();
		if (context.getUser() != null)
		{
			if (context.needsToSendSpeedMessage())
			{
				double moveSpeed = context.getUser().getMoveSpeed();
				Message message = WorldServerMessageFactory.generateActorMoveSpeedMessage(moveSpeed);
				context.enqueue(message);
				context.setNeedsToSendSpeedMessage(false);
			}
			queueForcesStateMessage(connection);
			queueNewlyVisibleMessages(connection);
			queueNewlyInvisibleMessages(connection);
			queueStateChangeMessages(connection);
		}
	}

	private void queueNewlyVisibleMessages(Connection<? extends T> connection) {
		Object o = connection.getMessageContext().getUser().getPreUpdateEvent(Vision.EVENT_NAME);
		if (o instanceof Vision)
		{
			Vision vision = (Vision) o;
			for (WorldObject object : vision.getNewlyVisibleObjects())
			{
				Message newlyVisibleMessage = WorldServerMessageFactory.generateNewlyVisibleMessage(object);
				connection.enqueueMessage(newlyVisibleMessage);
			}
		}
	}

	private void queueNewlyInvisibleMessages(Connection<? extends T> connection) {
		Object o = connection.getMessageContext().getUser().getPreUpdateEvent(Vision.EVENT_NAME);
		if (o instanceof Vision)
		{
			Vision vision = (Vision) o;
			for (WorldObject object : vision.getNewlyInvisibleObjects())
			{
				Message invisibleMessage = WorldServerMessageFactory.generateNewlyInvisibleMessage(object);
				connection.enqueueMessage(invisibleMessage);
			}
		}
	}

	private void queueStateChangeMessages(Connection<? extends T> connection) {
		Object o = connection.getMessageContext().getUser().getPreUpdateEvent(Vision.EVENT_NAME);
		if (o instanceof Vision)
		{
			Vision vision = (Vision) o;
			Set<WorldObject> visibles = vision.getVisibleObjects();
			visibles.stream().filter(object -> object instanceof Actor).forEach(object ->
				  queueActorStateChangeMessages(connection, (Actor) object));
		}
	}

	private void queueActorStateChangeMessages(Connection<? extends T> connection, Actor object) {
		List<Message> newState = WorldServerMessageFactory.generateChangeStateMessages(object);
		newState.forEach(connection::enqueueMessage);
	}

	private void queueForcesStateMessage(Connection<? extends T> connection) {
		MoveState forcedState = connection.getMessageContext().getUser().getForcedState();
		if (forcedState != null)
			connection.enqueueMessage(WorldServerMessageFactory.generateForcedStateMessage(forcedState));
	}
}
