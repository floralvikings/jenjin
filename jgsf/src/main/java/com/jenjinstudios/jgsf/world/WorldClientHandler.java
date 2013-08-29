package com.jenjinstudios.jgsf.world;

import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.jgsf.world.actor.Actor;

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
	 * Get the player's ID.
	 *
	 * @return The player id.
	 */
	public long getPlayerID()
	{
		return playerID;
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
	 * Get the actor of this client handler.
	 *
	 * @return The actor controlled by this client handler.
	 */
	public Actor getActor()
	{
		return actor;
	}
}
