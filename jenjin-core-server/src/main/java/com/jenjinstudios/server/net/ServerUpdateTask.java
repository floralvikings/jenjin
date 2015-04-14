package com.jenjinstudios.server.net;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerUpdateTask implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(ServerUpdateTask.class.getName());
	private final List<Runnable> repeatedTasks;
	private final Server server;

	/**
	 * Construct a new {@code ServerLoop} for the specified server.
	 * @param server The server for which this server loop works.
	 */
	@SuppressWarnings("unchecked")
	public ServerUpdateTask(Server server) {
		this.server = server;
		repeatedTasks = new LinkedList<>();
	}

	@Override
	public void run() {
		checkForNewClients();
		runRepeatedTasks();
	}

	private void checkForNewClients() {
		try
		{
			server.checkListenerForClients();
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Exception when checking for new clients", ex);
		}
	}

	private void runRepeatedTasks() {
		try
		{
			synchronized (repeatedTasks)
			{
				repeatedTasks.forEach(Runnable::run);
			}
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Exception when running repeated tasks.", ex);
		}
	}

	public void addRepeatedTask(Runnable r) {
		synchronized (repeatedTasks)
		{
			repeatedTasks.add(r);
		}
	}
}
