package com.jenjinstudios.jgcf;

import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.client.ClientActor;
import com.jenjinstudios.world.client.ClientObject;

import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Caleb Brinkman */
public class WorldClient extends Client
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldClient.class.getName());
	/** Actors other than the player. */
	private final TreeMap<Integer, ClientObject> nonPlayerObjects;
	/** The password used to login to the world. */
	private final String password;
	/** The loop used to update non-player objects. */
	private final UpdateLoop updateLoop;
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
		nonPlayerObjects = new TreeMap<>();
		this.password = password;
		// Create the update loop and add it to the task list.
		updateLoop = new UpdateLoop();
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
			Set<Integer> keys = nonPlayerObjects.keySet();
			for (int i : keys)
			{
				ClientObject currentObject = nonPlayerObjects.get(i);
				currentObject.update();
			}
		}
	}
}
