package com.jenjinstudios.world.server;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.event.PreUpdateEvent;
import com.jenjinstudios.world.server.message.WorldServerMessageFactory;
import com.jenjinstudios.world.state.MoveState;

import java.util.AbstractMap;
import java.util.List;

/**
 * Handles clients for a world server.
 * @author Caleb Brinkman
 */
public class WorldClientHandler extends ClientHandler
{
	private final WorldServerMessageFactory messageFactory;
	private Actor player;
	private boolean hasSentActorStepMessage;

	public WorldClientHandler(WorldServer<? extends WorldClientHandler> s, MessageIO messageIO) {
		super(s, messageIO);
		this.messageFactory = new WorldServerMessageFactory();
		setPlayer(new Actor("PLAYER"));
	}

	@Override
	public void update() {
		super.update();
		if (!hasSentActorStepMessage)
		{
			queueOutgoingMessage(getMessageFactory().generateActorMoveSpeedMessage(player.getMoveSpeed()));
			hasSentActorStepMessage = true;
		}
		queueForcesStateMessage();
		queueNewlyVisibleMessages();
		queueNewlyInvisibleMessages();
		queueStateChangeMessages();
	}

	@Override
	public WorldServerMessageFactory getMessageFactory() { return messageFactory; }

	public Actor getPlayer() { return player; }

	protected void setPlayer(Actor player) { this.player = player; }

	private void queueNewlyVisibleMessages() {
		PreUpdateEvent event = player.getPreUpdateEvent(Vision.EVENT_NAME);
		if (event != null)
		{
			Vision vision = (Vision) event;
			for (WorldObject object : vision.getNewlyVisibleObjects())
			{
				Message newlyVisibleMessage;
				newlyVisibleMessage = getMessageFactory().generateNewlyVisibleMessage(object);
				queueOutgoingMessage(newlyVisibleMessage);
			}
		}
	}

	private void queueNewlyInvisibleMessages() {
		PreUpdateEvent event = player.getPreUpdateEvent(Vision.EVENT_NAME);
		if (event != null)
		{
			Vision vision = (Vision) event;
			for (WorldObject object : vision.getNewlyInvisibleObjects())
			{
				Message newlyInvisibleMessage = getMessageFactory().generateNewlyInvisibleMessage(object);
				queueOutgoingMessage(newlyInvisibleMessage);
			}
		}
	}

	private void queueStateChangeMessages() {
		PreUpdateEvent event = player.getPreUpdateEvent(Vision.EVENT_NAME);
		if (event != null)
		{
			Vision vision = (Vision) event;
			AbstractMap<Integer, WorldObject> visibles = vision.getVisibleObjects();
			visibles.values().stream().filter(object -> object instanceof Actor).forEach(object ->
				  queueActorStateChangeMessages((Actor) object));
		}
	}

	private void queueActorStateChangeMessages(Actor object) {
		List<Message> newState = getMessageFactory().generateChangeStateMessages(object);
		newState.forEach(this::queueOutgoingMessage);
	}

	private void queueForcesStateMessage() {
		MoveState forcedState = player.getForcedState();
		if (forcedState != null)
			queueOutgoingMessage(getMessageFactory().generateForcedStateMessage(forcedState));
	}
}
