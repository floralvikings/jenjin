package com.jenjinstudios.jgcf;

import java.io.IOException;
import java.util.TimerTask;

/**
 * The ClientLoop class is essentially what amounts to the output thread.
 *
 * @author Caleb Brinkman
 */

public class ClientLoop extends TimerTask
{
	/** The Client for this loop. */
	private final Client client;

	/**
	 * Construct a ClientLoop for the given client.
	 *
	 * @param client The client for this ClientLoop
	 */
	public ClientLoop(Client client)
	{
		this.client = client;
	}

	@Override
	public void run()
	{
		for (Runnable r : client.getRepeatedSyncedTasks())
			r.run();
		for(Runnable r : client.getSyncedTasks())
			r.run();
		try
		{
			client.sendAllMessages();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}