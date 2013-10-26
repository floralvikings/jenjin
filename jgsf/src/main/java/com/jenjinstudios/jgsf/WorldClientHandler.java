package com.jenjinstudios.jgsf;

import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;

import java.io.IOException;
import java.net.Socket;

/**
 * Handles clients for a world server.
 *
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
	 *
	 * @param s  The server for which this handler works.
	 * @param sk The socket used to communicate with the client.
	 *
	 * @throws java.io.IOException If the socket is unable to connect.
	 */
	public WorldClientHandler(WorldServer s, Socket sk) throws IOException
	{
		super(s, sk);
		server = s;
		queueMessage(generateActorStepLengthMessage());
	}

	/**
	 * Get the actor of this client handler.
	 *
	 * @return The actor controlled by this client handler.
	 */
	public Actor getActor()
	{
		return actor;
	}

	/**
	 * Set the Actor managed by this handler.
	 *
	 * @param actor The actor to be managed by this handler.
	 */
	public void setActor(Actor actor)
	{
		this.actor = actor;
		setUsername(actor.getName());
		setPlayerID(actor.getId());
	}

	/**
	 * Set the player ID, id it is not already set.
	 *
	 * @param id The new ID.
	 */
	public void setPlayerID(long id)
	{
		if (playerID == -1)
			playerID = id;
	}

	@Override
	public void update()
	{
		if (actor == null)
			return;

		queueForcesStateMessage();
		queueNewlyVisibleMessages();
		queueNewlyInvisibleMessages();
		queueStateChangeMessages();
	}

	/** Generate and queue messages for newly visible objects. */
	private void queueNewlyVisibleMessages()
	{
		for (WorldObject object : actor.getNewlyVisibleObjects())
		{
			Message newlyVisibleMessage;
			newlyVisibleMessage = generateNewlyVisibleMessage(object);
			queueMessage(newlyVisibleMessage);
		}
	}

	/**
	 * Generate an appropriate message for a newly visible object.
	 *
	 * @param object The object.
	 *
	 * @return The message.
	 */
	private Message generateNewlyVisibleMessage(WorldObject object)
	{
		Message newlyVisibleMessage;
		if (object instanceof Actor)
		{
			newlyVisibleMessage = generateActorVisibleMessage((Actor) object);
		} else
		{
			newlyVisibleMessage = generateObjectVisibleMessage(object);
		}
		return newlyVisibleMessage;
	}

	/**
	 * Generate an ActorVisibleMessage using the given actor.
	 *
	 * @param newlyVisible The Actor used to generate the message.
	 *
	 * @return A {@code Message} for the newly visible actor.
	 */
	private Message generateActorVisibleMessage(Actor newlyVisible)
	{
		Message newlyVisibleMessage;
		newlyVisibleMessage = new Message("ActorVisibleMessage");
		newlyVisibleMessage.setArgument("name", newlyVisible.getName());
		newlyVisibleMessage.setArgument("id", newlyVisible.getId());
		newlyVisibleMessage.setArgument("xCoordinate", newlyVisible.getVector2D().getXCoordinate());
		newlyVisibleMessage.setArgument("zCoordinate", newlyVisible.getVector2D().getZCoordinate());
		newlyVisibleMessage.setArgument("relativeAngle", newlyVisible.getCurrentMoveState().relativeAngle);
		newlyVisibleMessage.setArgument("angle", newlyVisible.getMoveAngle());
		newlyVisibleMessage.setArgument("stepsTaken", newlyVisible.getStepsTaken());
		newlyVisibleMessage.setArgument("stepsUntilChange", newlyVisible.getCurrentMoveState().stepsUntilChange);
		return newlyVisibleMessage;
	}

	/**
	 * Generate an ObjectVisibleMessage using the given actor.
	 *
	 * @param object The Actor used to generate the message.
	 *
	 * @return A {@code Message} for the newly visible object.
	 */
	private Message generateObjectVisibleMessage(WorldObject object)
	{
		Message newlyVisibleMessage;
		newlyVisibleMessage = new Message("ObjectVisibleMessage");
		newlyVisibleMessage.setArgument("name", object.getName());
		newlyVisibleMessage.setArgument("id", object.getId());
		newlyVisibleMessage.setArgument("xCoordinate", object.getVector2D().getXCoordinate());
		newlyVisibleMessage.setArgument("zCoordinate", object.getVector2D().getZCoordinate());
		return newlyVisibleMessage;
	}

	/** Generate and queue messages for newly invisible objects. */
	private void queueNewlyInvisibleMessages()
	{
		for (WorldObject object : actor.getNewlyInvisibleObjects())
		{
			Message newlyInvisibleMessage = generateNewlyInvisibleMessage(object);
			queueMessage(newlyInvisibleMessage);
		}
	}

	/**
	 * Generate a NewlyIvisibleObjectMessage for the given object.
	 *
	 * @param object The {@code WorldObject} that is newly invisible.
	 *
	 * @return A {@code Message} for the newly invisible object.
	 */
	private Message generateNewlyInvisibleMessage(WorldObject object)
	{
		Message newlyInvisibleMessage = new Message("ObjectInvisibleMessage");
		newlyInvisibleMessage.setArgument("id", object.getId());
		return newlyInvisibleMessage;
	}

	/** Generate and queue messages for actors with changed states. */
	private void queueStateChangeMessages()
	{
		for (WorldObject object : actor.getVisibleObjects())
		{
			Actor changedActor;
			if (object instanceof Actor && (changedActor = (Actor) object).isNewState())
			{
				Message newState = generateChangeStateMessage(changedActor);
				queueMessage(newState);
			}
		}
	}

	/**
	 * Generate a state change message for the given actor.
	 *
	 * @param changedActor The actor with a new state.
	 *
	 * @return The state change message.
	 */
	private Message generateChangeStateMessage(Actor changedActor)
	{
		Message newState = new Message("StateChangeMessage");
		newState.setArgument("id", changedActor.getId());
		newState.setArgument("relativeAngle", changedActor.getCurrentMoveState().relativeAngle);
		newState.setArgument("angle", changedActor.getCurrentMoveState().moveAngle);
		newState.setArgument("stepsUntilChange", changedActor.getCurrentMoveState().stepsUntilChange);
		return newState;
	}

	@Override
	public WorldServer getServer()
	{
		return server;
	}

	/** Generate and queue a ForcedStateMessage if necessary. */
	private void queueForcesStateMessage()
	{
		if (actor.isForcedState())
			queueMessage(generateForcedStateMessage());
	}

	/**
	 * Generate a forced state message.
	 *
	 * @return A forced state message for the actor's state at the beginning of this server "tick".
	 */
	private Message generateForcedStateMessage()
	{
		Message forcedStateMessage = new Message("ForceStateMessage");
		forcedStateMessage.setArgument("relativeAngle", actor.getMoveDirection());
		forcedStateMessage.setArgument("angle", actor.getMoveAngle());
		forcedStateMessage.setArgument("xCoordinate", actor.getVector2D().getXCoordinate());
		forcedStateMessage.setArgument("zCoordinate", actor.getVector2D().getZCoordinate());
		forcedStateMessage.setArgument("timeOfForce", server.getCycleStartTime());
		return forcedStateMessage;
	}

	/**
	 * Generate a step length message.
	 *
	 * @return The message.
	 */
	private Message generateActorStepLengthMessage()
	{
		Message stepLengthMessage = new Message("ActorStepMessage");
		stepLengthMessage.setArgument("stepLength", Actor.STEP_LENGTH);
		return stepLengthMessage;
	}

	/**
	 * Get the player associated with this client handler.
	 *
	 * @return The player associated with this client handler.
	 */
	public Actor getPlayer()
	{
		return actor;
	}
}
