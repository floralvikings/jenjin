package com.jenjinstudios.jgcf;

import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.ClientObject;

import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The WorldClient class is used to connect to a WorldServer and stores information about the environment immediately
 * surrounding the player.
 *
 * @author Caleb Brinkman
 */
public class WorldClient extends Client
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldClient.class.getName());
	/** Actors other than the player. */
	private final TreeMap<Integer, ClientObject> visibleObjects;
	/** The password used to login to the world. */
	private final String password;
	/** The actor representing the player controlled by this client. */
	private ClientActor player;

	/**
	 * Construct a client connecting to the given address over the given port.  This client <i>must</i> have a username
	 * and password.
	 *
	 * @param address  The address to which this client will attempt to connect.
	 * @param port     The port over which this client will attempt to connect.
	 * @param username The username that will be used by this client.
	 * @param password The password that will be used by this client.
	 */
	public WorldClient(String address, int port, String username, String password)
	{
		super(address, port, username, password);
		visibleObjects = new TreeMap<>();
		this.password = password;
		// Create the update loop and add it to the task list.
		/* The loop used to update non-player objects. */
		UpdateLoop updateLoop = new UpdateLoop();
		addRepeatedTask(updateLoop);
	}

	/** Log the player into the world, and set the returned player as the actor for this client. */
	public void loginToWorld()
	{
		Message loginRequest = new Message("WorldLoginRequest");
		loginRequest.setArgument("username", getUsername());
		loginRequest.setArgument("password", password);

		setReceivedLoginResponse(false);
		sendMessage(loginRequest);
		while (!hasReceivedLoginResponse())
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
	}

	/** Log the player out of the world.  Blocks until logout is confirmed. */
	public void logoutOfWorld()
	{
		// Create the message.
		Message logoutRequest = new Message("WorldLogoutRequest");

		// Send the request, continue when response is received.
		setReceivedLogoutResponse(false);
		sendMessage(logoutRequest);
		while (!hasReceivedLogoutResponse())
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
	}

	/**
	 * Add an object to the list of visible objects.  This method should be called synchronously.
	 *
	 * @param object The object to add to the visible objects list.
	 */
	public void addNewVisible(ClientObject object)
	{
		visibleObjects.put(object.getId(), object);
	}

	/**
	 * Remove an object from the player's view.
	 *
	 * @param object the object to remove.
	 */
	public void removeVisible(ClientObject object)
	{
		visibleObjects.remove(object.getId());
	}

	/**
	 * Get the player associated with this client.
	 *
	 * @return The player associated with this client.
	 */
	public ClientActor getPlayer()
	{
		return player;
	}

	/**
	 * Set the player being controlled by this client.
	 *
	 * @param player The player to be controlled by this client.
	 */
	public void setPlayer(ClientActor player)
	{
		if (this.player != null)
			throw new IllegalStateException("Player already set!");
		this.player = player;
	}

	/** The UpdateLoop class is used to update all wold objects. */
	private class UpdateLoop implements Runnable
	{
		/**
		 * When an object implementing interface {@code Runnable} is used
		 * to create a thread, starting the thread causes the object's
		 * {@code run} method to be called in that separately executing
		 * thread.
		 * <p/>
		 * The general contract of the method {@code run} is that it may
		 * take any action whatsoever.
		 *
		 * @see Thread#run()
		 */
		@Override
		public void run()
		{
			Set<Integer> keys = visibleObjects.keySet();
			for (int i : keys)
			{
				ClientObject currentObject = visibleObjects.get(i);
				currentObject.update();
			}
		}
	}
}
