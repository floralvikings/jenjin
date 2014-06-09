package com.jenjinstudios.net;

import java.util.TimerTask;

/**
 * The ClientLoop class is essentially what amounts to the output thread.
 * @author Caleb Brinkman
 */

class ClientLoop extends TimerTask
{
	/** The Client for this loop. */
	private final Client client;

	/**
	 * Construct a ClientLoop for the given client.
	 * @param client The client for this ClientLoop
	 */
	public ClientLoop(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		client.runRepeatedSyncedTasks();
		client.runSyncedTasks();
		client.sendAllMessages();
	}

}