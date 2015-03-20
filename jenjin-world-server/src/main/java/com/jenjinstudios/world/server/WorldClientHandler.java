package com.jenjinstudios.world.server;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
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
public class WorldClientHandler extends ClientHandler
{
	private final WorldServerMessageFactory messageFactory;
	private boolean hasSentActorStepMessage;

	public WorldClientHandler(WorldServer s, MessageIO messageIO) {
		super(s, messageIO);
		this.messageFactory = new WorldServerMessageFactory();
	}

	@Override
	public void update() {
		super.update();
		if (getUser() != null)
		{
			if (!hasSentActorStepMessage)
			{
				getMessageIO().queueOutgoingMessage(getMessageFactory().generateActorMoveSpeedMessage(getUser()
					  .getMoveSpeed()));
				hasSentActorStepMessage = true;
			}
			queueForcesStateMessage();
			queueNewlyVisibleMessages();
			queueNewlyInvisibleMessages();
			queueStateChangeMessages();
		}
	}

	public WorldServerMessageFactory getMessageFactory() { return messageFactory; }

	public Player getUser() { return (Player) super.getUser(); }

	private void queueNewlyVisibleMessages() {
		Object o = getUser().getPreUpdateEvent(Vision.EVENT_NAME);
		if (o != null && o instanceof Vision)
		{
			Vision vision = (Vision) o;
			for (WorldObject object : vision.getNewlyVisibleObjects())
			{
				Message newlyVisibleMessage;
				newlyVisibleMessage = getMessageFactory().generateNewlyVisibleMessage(object);
				getMessageIO().queueOutgoingMessage(newlyVisibleMessage);
			}
		}
	}

	private void queueNewlyInvisibleMessages() {
		Object o = getUser().getPreUpdateEvent(Vision.EVENT_NAME);
		if (o != null && o instanceof Vision)
		{
			Vision vision = (Vision) o;
			for (WorldObject object : vision.getNewlyInvisibleObjects())
			{
				Message newlyInvisibleMessage = getMessageFactory().generateNewlyInvisibleMessage(object);
				getMessageIO().queueOutgoingMessage(newlyInvisibleMessage);
			}
		}
	}

	private void queueStateChangeMessages() {
		Object o = getUser().getPreUpdateEvent(Vision.EVENT_NAME);
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
		newState.forEach(getMessageIO()::queueOutgoingMessage);
	}

	private void queueForcesStateMessage() {
		MoveState forcedState = getUser().getForcedState();
		if (forcedState != null)
			getMessageIO().queueOutgoingMessage(getMessageFactory().generateForcedStateMessage(forcedState));
	}
}
