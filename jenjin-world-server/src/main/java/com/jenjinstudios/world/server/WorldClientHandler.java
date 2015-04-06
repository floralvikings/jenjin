package com.jenjinstudios.world.server;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.server.message.WorldServerMessageFactory;
import com.jenjinstudios.world.state.MoveState;

import java.util.List;
import java.util.Set;

/**
 * Handles clients for a world server.
 *
 * @author Caleb Brinkman
 */
public class WorldClientHandler extends ClientHandler<WorldServerMessageContext>
{
	private boolean hasSentActorStepMessage;

	public WorldClientHandler(WorldServer s, MessageStreamPair messageStreamPair, WorldServerMessageContext context) {
		super(s, messageStreamPair, context);
	}

	@Override
	public void update() {
		super.update();
		if (getMessageContext().getUser() != null)
		{
			if (!hasSentActorStepMessage)
			{
				double moveSpeed = getMessageContext().getUser().getMoveSpeed();
				Message message = WorldServerMessageFactory.generateActorMoveSpeedMessage(moveSpeed);
				enqueueMessage(message);
				hasSentActorStepMessage = true;
			}
			queueForcesStateMessage();
			queueNewlyVisibleMessages();
			queueNewlyInvisibleMessages();
			queueStateChangeMessages();
		}
	}

	public Player getUser() { return getMessageContext().getUser(); }

	private void queueNewlyVisibleMessages() {
		Object o = getMessageContext().getUser().getPreUpdateEvent(Vision.EVENT_NAME);
		if (o != null && o instanceof Vision)
		{
			Vision vision = (Vision) o;
			for (WorldObject object : vision.getNewlyVisibleObjects())
			{
				Message newlyVisibleMessage;
				newlyVisibleMessage = WorldServerMessageFactory.generateNewlyVisibleMessage(object);
				enqueueMessage(newlyVisibleMessage);
			}
		}
	}

	private void queueNewlyInvisibleMessages() {
		Object o = getMessageContext().getUser().getPreUpdateEvent(Vision.EVENT_NAME);
		if (o != null && o instanceof Vision)
		{
			Vision vision = (Vision) o;
			for (WorldObject object : vision.getNewlyInvisibleObjects())
			{
				Message newlyInvisibleMessage = WorldServerMessageFactory.generateNewlyInvisibleMessage(object);
				enqueueMessage(newlyInvisibleMessage);
			}
		}
	}

	private void queueStateChangeMessages() {
		Object o = getMessageContext().getUser().getPreUpdateEvent(Vision.EVENT_NAME);
		if (o != null && o instanceof Vision)
		{
			Vision vision = (Vision) o;
			Set<WorldObject> visibles = vision.getVisibleObjects();
			visibles.stream().filter(object -> object instanceof Actor).forEach(object ->
				  queueActorStateChangeMessages((Actor) object));
		}
	}

	private void queueActorStateChangeMessages(Actor object) {
		List<Message> newState = WorldServerMessageFactory.generateChangeStateMessages(object);
		newState.forEach(this::enqueueMessage);
	}

	private void queueForcesStateMessage() {
		MoveState forcedState = getMessageContext().getUser().getForcedState();
		if (forcedState != null)
			enqueueMessage(WorldServerMessageFactory.generateForcedStateMessage(forcedState));
	}
}
