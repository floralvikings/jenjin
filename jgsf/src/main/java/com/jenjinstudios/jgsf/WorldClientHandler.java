package com.jenjinstudios.jgsf;

import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;

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
	/** The ID of the player controlled by this clienthandler. */
	private long playerID = -1;
	/** The Actor managed by this handler. */
	private Actor actor;

	/**
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary to
	 * send the client a FirstConnectResponse message with the server's UPS
	 * @param s The server for which this handler works.
	 * @param sk The socket used to communicate with the client.
	 * @throws java.io.IOException If the socket is unable to connect.
	 */
	public WorldClientHandler(WorldServer s, Socket sk) throws IOException {
		super(s, sk);
		server = s;
		queueMessage(WorldMessageGenerator.generateActorStepLengthMessage());
	}

	/**
	 * Get the actor of this client handler.
	 * @return The actor controlled by this client handler.
	 */
	public Actor getActor() {
		return actor;
	}

	/**
	 * Set the Actor managed by this handler.
	 * @param actor The actor to be managed by this handler.
	 */
	public void setActor(Actor actor) {
		this.actor = actor;
		setUsername(actor.getName());
		setPlayerID(actor.getId());
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

		if (actor == null)
			return;

		queueForcesStateMessage();
		queueNewlyVisibleMessages();
		queueNewlyInvisibleMessages();
		queueStateChangeMessages();
	}

	/** Generate and queue messages for newly visible objects. */
	private void queueNewlyVisibleMessages() {
		for (WorldObject object : actor.getNewlyVisibleObjects())
		{
			Message newlyVisibleMessage;
			newlyVisibleMessage = WorldMessageGenerator.generateNewlyVisibleMessage(object);
			queueMessage(newlyVisibleMessage);
		}
	}

	/** Generate and queue messages for newly invisible objects. */
	private void queueNewlyInvisibleMessages() {
		for (WorldObject object : actor.getNewlyInvisibleObjects())
		{
			Message newlyInvisibleMessage = generateNewlyInvisibleMessage(object);
			queueMessage(newlyInvisibleMessage);
		}
	}

	/**
	 * Generate a NewlyIvisibleObjectMessage for the given object.
	 * @param object The {@code WorldObject} that is newly invisible.
	 * @return A {@code Message} for the newly invisible object.
	 */
	private static Message generateNewlyInvisibleMessage(WorldObject object) {
		Message newlyInvisibleMessage = new Message("ObjectInvisibleMessage");
		newlyInvisibleMessage.setArgument("id", object.getId());
		return newlyInvisibleMessage;
	}

	/** Generate and queue messages for actors with changed states. */
	private void queueStateChangeMessages() {
		for (WorldObject object : actor.getVisibleObjects())
		{
			Actor changedActor;
			if (object instanceof Actor && (changedActor = (Actor) object).isNewState())
			{
				Message newState = WorldMessageGenerator.generateChangeStateMessage(changedActor);
				queueMessage(newState);
			}
		}
	}

	@Override
	public WorldServer getServer() {
		return server;
	}

	/** Generate and queue a ForcedStateMessage if necessary. */
	private void queueForcesStateMessage() {
		if (actor.isForcedState())
			queueMessage(WorldMessageGenerator.generateForcedStateMessage(actor, server));
	}

	/**
	 * Get the player associated with this client handler.
	 * @return The player associated with this client handler.
	 */
	public Actor getPlayer() {
		return actor;
	}
}
