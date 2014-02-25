package com.jenjinstudios.world;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.net.ClientHandler;
import com.jenjinstudios.world.util.WorldServerMessageGenerator;

import java.io.IOException;
import java.net.Socket;

/**
 * Handles clients for a world server.
 * @author Caleb Brinkman
 */
public class WorldClientHandler extends ClientHandler
{
	/** The WorldServer owning this handler. */
	private final WorldServer server;
	/** The ID of the player controlled by this client handler. */
	private long playerID = -1;
	/** The Actor managed by this handler. */
	private Player player;

	/**
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary to
	 * send the client a FirstConnectResponse message with the server's UPS
	 * @param s The server for which this handler works.
	 * @param sk The socket used to communicate with the client.
	 * @param messageRegistry The MessageRegistry for this ClientHandler.
	 * @throws java.io.IOException If the socket is unable to connect.
	 */
	public WorldClientHandler(WorldServer s, Socket sk, MessageRegistry messageRegistry) throws IOException {
		super(s, sk, messageRegistry);
		server = s;
		queueMessage(WorldServerMessageGenerator.generateActorStepLengthMessage(this));
	}

	/**
	 * Set the Actor managed by this handler.
	 * @param player The player to be managed by this handler.
	 */
	public void setPlayer(Player player) {
		this.player = player;
		setUsername(player.getName());
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
			newlyVisibleMessage = WorldServerMessageGenerator.generateNewlyVisibleMessage(this, object);
			queueMessage(newlyVisibleMessage);
		}
	}

	/** Generate and queue messages for newly invisible objects. */
	private void queueNewlyInvisibleMessages() {
		for (WorldObject object : player.getNewlyInvisibleObjects())
		{
			Message newlyInvisibleMessage = WorldServerMessageGenerator.generateNewlyInvisibleMessage(this, object);
			queueMessage(newlyInvisibleMessage);
		}
	}

	/** Generate and queue messages for actors with changed states. */
	private void queueStateChangeMessages() {
		for (WorldObject object : player.getVisibleObjects().values())
		{
			Actor changedActor;
			if (object instanceof Actor && (changedActor = (Actor) object).isNewState())
			{
				Message newState = WorldServerMessageGenerator.generateChangeStateMessage(this, changedActor);
				queueMessage(newState);
			}
		}
	}

	/** Generate and queue a ForcedStateMessage if necessary. */
	private void queueForcesStateMessage() {
		if (player.isForcedState())
			queueMessage(WorldServerMessageGenerator.generateForcedStateMessage(this, player, server));
	}
}
