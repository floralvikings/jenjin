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
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary
	 * to send the client a FirstConnectResponse message with the server's UPS
	 *
	 * @param s  The server for which this handler works.
	 * @param sk The socket used to communicate with the client.
	 * @throws java.io.IOException If the socket is unable to connect.
	 */
	public WorldClientHandler(WorldServer s, Socket sk) throws IOException
	{
		super(s, sk);
		server = s;
	}

	@Override
	public WorldServer getServer()
	{
		return server;
	}

	/**
	 * Set the player ID, id it is not already set.
	 *
	 * @param id The new ID.
	 */
	public void setPlayerID(long id)
	{
		if (playerID != -1)
			playerID = id;
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

	@Override
	public void update()
	{
		if (actor == null)
			return;

		for (WorldObject object : actor.getNewlyVisibleObjects())
		{
			Message newlyVisibleMessage;
			if (object instanceof Actor)
			{
				newlyVisibleMessage = new Message("ActorVisibleMessage");
				Actor newActor = (Actor) object;
				newlyVisibleMessage.setArgument("name", newActor.getName());
				newlyVisibleMessage.setArgument("id", newActor.getId());
				newlyVisibleMessage.setArgument("xCoordinate", newActor.getVector2D().getXCoordinate());
				newlyVisibleMessage.setArgument("zCoordinate", newActor.getVector2D().getZCoordinate());
				newlyVisibleMessage.setArgument("direction", newActor.getCurrentDirection());
				newlyVisibleMessage.setArgument("angle", newActor.getCurrentAngle());
				newlyVisibleMessage.setArgument("stepsTaken", newActor.getStepsTaken());
				newlyVisibleMessage.setArgument("stepsUntilChange", newActor.getCurrentMoveState().stepsUntilChange);

			} else
			{
				newlyVisibleMessage = new Message("ObjectVisibleMessage");
				newlyVisibleMessage.setArgument("name", object.getName());
				newlyVisibleMessage.setArgument("id", object.getId());
				newlyVisibleMessage.setArgument("xCoordinate", object.getVector2D().getXCoordinate());
				newlyVisibleMessage.setArgument("zCoordinate", object.getVector2D().getZCoordinate());
			}
			queueMessage(newlyVisibleMessage);
		}

		for (WorldObject object : actor.getNewlyInvisibleObjects())
		{
			Message newlyInvisibleMessage = new Message("ObjectInvisibleMessage");
			newlyInvisibleMessage.setArgument("id", object.getId());
			queueMessage(newlyInvisibleMessage);
		}

		for (WorldObject object : actor.getVisibleObjects())
		{
			Actor changedActor;
			if (!(object instanceof Actor) || (changedActor = (Actor) object).isNewState())
				continue;
			Message newState = new Message("StateChangeMessage");
			newState.setArgument("id", changedActor.getId());
			newState.setArgument("direction", changedActor.getCurrentDirection());
			newState.setArgument("angle", changedActor.getCurrentAngle());
			newState.setArgument("stepsUntilChange", changedActor.getStepsInLastMove());
		}
	}
}
