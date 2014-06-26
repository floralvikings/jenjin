package com.jenjinstudios.world;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.world.message.WorldServerMessageFactory;
import com.jenjinstudios.world.state.MoveState;

import java.io.IOException;
import java.util.List;

/**
 * Handles clients for a world server.
 * @author Caleb Brinkman
 */
public class WorldClientHandler extends ClientHandler
{
	/** The WorldServer owning this handler. */
	private final WorldServer server;
	/** The MessageFactory owned by this client handler. */
	private final WorldServerMessageFactory messageFactory;
	/** The ID of the player controlled by this client handler. */
	private long playerID = -1;
	/** The Actor managed by this handler. */
	private Player player;
	/** Whether this handler has sent the actor step length message. */
	private boolean hasSentActorStepMessage;

	/**
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary to
	 * send the client a FirstConnectResponse message with the server's UPS
	 * @param s The server for which this handler works.
	 * @throws java.io.IOException If the socket is unable to connect.
	 */
	public WorldClientHandler(WorldServer s, MessageIO messageIO) throws IOException {
		super(s, messageIO);
		server = s;
		this.messageFactory = new WorldServerMessageFactory(this, server.getMessageRegistry());
	}

	/**
	 * Set the Actor managed by this handler.
	 * @param player The player to be managed by this handler.
	 */
	public void setPlayer(Player player) {
		this.player = player;
		setPlayerID(player.getId());
	}

	/**
	 * Set the player ID, id it is not already set.
	 * @param id The new ID.
	 */
	public void setPlayerID(long id) {
		if (playerID == -1)
			playerID = id;
	}

	@Override
	public void update() {
		super.update();

		if (!hasSentActorStepMessage)
		{
			queueOutgoingMessage(getMessageFactory().generateActorMoveSpeedMessage());
			hasSentActorStepMessage = true;
		}

		if (player == null)
			return;

		queueForcesStateMessage();
		queueNewlyVisibleMessages();
		queueNewlyInvisibleMessages();
		queueStateChangeMessages();
	}

	@Override
	public WorldServer getServer() { return server; }

	/**
	 * Get the player associated with this client handler.
	 * @return The player associated with this client handler.
	 */
	public Player getPlayer() { return player; }

	/** Generate and queue messages for newly visible objects. */
	private void queueNewlyVisibleMessages() {
		for (WorldObject object : player.getNewlyVisibleObjects())
		{
			Message newlyVisibleMessage;
			newlyVisibleMessage = getMessageFactory().generateNewlyVisibleMessage(object);
			queueOutgoingMessage(newlyVisibleMessage);
		}
	}

	/** Generate and queue messages for newly invisible objects. */
	private void queueNewlyInvisibleMessages() {
		for (WorldObject object : player.getNewlyInvisibleObjects())
		{
			Message newlyInvisibleMessage = getMessageFactory().generateNewlyInvisibleMessage(object);
			queueOutgoingMessage(newlyInvisibleMessage);
		}
	}

	/** Generate and queue messages for actors with changed states. */
	private void queueStateChangeMessages() {
		for (WorldObject object : player.getVisibleObjects().values())
		{
			Actor changedActor;
			if (object instanceof Actor)
			{
				changedActor = (Actor) object;
				List<Message> newState = getMessageFactory().generateChangeStateMessage(changedActor);
				for (Message m : newState) { queueOutgoingMessage(m);}
			}
		}
	}

	/** Generate and queue a ForcedStateMessage if necessary. */
	private void queueForcesStateMessage() {
		MoveState forcedState = player.getForcedState();
		if (forcedState != null)
			queueOutgoingMessage(getMessageFactory().generateForcedStateMessage(forcedState, server));
	}

	@Override
	public WorldServerMessageFactory getMessageFactory() { return messageFactory; }
}
