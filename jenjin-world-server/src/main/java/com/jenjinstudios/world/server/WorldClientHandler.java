package com.jenjinstudios.world.server;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.server.message.WorldServerMessageFactory;
import com.jenjinstudios.world.state.MoveState;

import java.io.IOException;
import java.util.List;

/**
 * Handles clients for a world server.
 * @author Caleb Brinkman
 */
public class WorldClientHandler extends ClientHandler
{
	private final WorldServerMessageFactory messageFactory;
	private Player player;
	private boolean hasSentActorStepMessage;

	public WorldClientHandler(WorldServer s, MessageIO messageIO) throws IOException {
		super(s, messageIO);
		this.messageFactory = new WorldServerMessageFactory(this, getServer().getMessageRegistry());
	}

	@Override
	public void update() {
		super.update();
		if (player != null)
		{
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
	}

	@Override
	public WorldServerMessageFactory getMessageFactory() { return messageFactory; }

	@Override
	public WorldServer getServer() { return (WorldServer) super.getServer(); }

	public Player getPlayer() { return player; }

	public void setPlayer(Player player) { this.player = player; }

	private void queueNewlyVisibleMessages() {
		for (WorldObject object : player.getNewlyVisibleObjects())
		{
			Message newlyVisibleMessage;
			newlyVisibleMessage = getMessageFactory().generateNewlyVisibleMessage(object);
			queueOutgoingMessage(newlyVisibleMessage);
		}
	}

	private void queueNewlyInvisibleMessages() {
		for (WorldObject object : player.getNewlyInvisibleObjects())
		{
			Message newlyInvisibleMessage = getMessageFactory().generateNewlyInvisibleMessage(object);
			queueOutgoingMessage(newlyInvisibleMessage);
		}
	}

	private void queueStateChangeMessages() {
		player.getVisibleObjects().values().stream().filter(object -> object instanceof Actor).forEach(object ->
			  queueActorStateChangeMessages((Actor) object));
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
