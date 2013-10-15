package com.jenjinstudios.jgcf;

import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.ClientObject;
import com.jenjinstudios.world.ClientPlayer;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;
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
public class WorldClient extends AuthClient
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldClient.class.getName());
	/** Actors other than the player. */
	private final TreeMap<Integer, ClientObject> visibleObjects;
	/** The password used to login to the world. */
	private final String password;
	/** The actor representing the player controlled by this client. */
	private ClientPlayer player;

	/**
	 * Construct a client connecting to the given address over the given port.  This client <i>must</i> have a username and
	 * password.
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
		addRepeatedTask(new Runnable()
		{
			@Override
			public void run()
			{
				Set<Integer> keys = visibleObjects.keySet();
				for (int i : keys)
				{
					ClientObject currentObject = visibleObjects.get(i);
					currentObject.update();
				}
				if (player != null)
				{
					LinkedList<MoveState> newStates = player.getSavedStates();
					while (!newStates.isEmpty())
						sendStateChangeRequest(newStates.remove());
				}
			}
		});
	}

	/** Log the player into the world, and set the returned player as the actor for this client. */
	public void blockingLoginToWorld()
	{
		sendLoginRequest();
		while (!hasReceivedLoginResponse())
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
	}

	/** Send a LoginRequest to the server. */
	private void sendLoginRequest()
	{
		Message loginRequest = generateLoginRequest();

		setReceivedLoginResponse(false);
		sendMessage(loginRequest);
	}

	/**
	 * Generate a LoginRequest message.
	 *
	 * @return The LoginRequest message.
	 */
	private Message generateLoginRequest()
	{
		Message loginRequest = new Message("WorldLoginRequest");
		loginRequest.setArgument("username", getUsername());
		loginRequest.setArgument("password", password);
		return loginRequest;
	}

	/** Log the player out of the world.  Blocks until logout is confirmed. */
	public void blockingLogoutOfWorld()
	{
		sendLogoutRequest();

		while (!hasReceivedLogoutResponse())
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
	}

	/** Send a LogoutRequest to the server. */
	private void sendLogoutRequest()
	{
		Message logoutRequest = new Message("WorldLogoutRequest");

		// Send the request, continue when response is received.
		setReceivedLogoutResponse(false);
		sendMessage(logoutRequest);
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
	 * @param id the id of the object to remove.
	 */
	public void removeVisible(int id)
	{
		visibleObjects.remove(id);
	}

	/**
	 * Get the map of visible objects.
	 *
	 * @return The map of visible objects.
	 */
	public TreeMap<Integer, ClientObject> getVisibleObjects()
	{
		return visibleObjects;
	}

	/**
	 * Get the player associated with this client.
	 *
	 * @return The player (ClientActor) associated with this client.
	 */
	public ClientPlayer getPlayer()
	{
		return player;
	}

	/**
	 * Set the player being controlled by this client.
	 *
	 * @param player The player to be controlled by this client.
	 */
	public void setPlayer(ClientPlayer player)
	{
		if (this.player != null)
			throw new IllegalStateException("Player already set!");
		this.player = player;
	}

	/**
	 * Send a state change request to the server.
	 *
	 * @param moveState The move state used to generate the request.
	 */
	private void sendStateChangeRequest(MoveState moveState)
	{
		Message stateChangeRequest = generateStateChangeRequest(moveState);
		sendMessage(stateChangeRequest);
	}

	/**
	 * Generate a state change request for the given move state.
	 *
	 * @param moveState The state used to generate a state change request.
	 *
	 * @return The generated message.
	 */
	private Message generateStateChangeRequest(MoveState moveState)
	{
		Message stateChangeRequest = new Message("StateChangeRequest");
		stateChangeRequest.setArgument("direction", moveState.direction);
		stateChangeRequest.setArgument("angle", moveState.moveAngle);
		stateChangeRequest.setArgument("stepsUntilChange", moveState.stepsUntilChange);
		return stateChangeRequest;
	}

	/**
	 * Get the ClientObject with the given ID.
	 *
	 * @param id The ID of the object to retrieve.
	 *
	 * @return The object with the given ID.
	 */
	public ClientObject getObject(int id)
	{
		return visibleObjects.get(id);
	}
}
