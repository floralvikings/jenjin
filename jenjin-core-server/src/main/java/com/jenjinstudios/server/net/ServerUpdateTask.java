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
		runRepeatedTasks();
	}

	private void runRepeatedTasks() {
		try
		{
			synchronized (repeatedTasks)
			{
				repeatedTasks.forEach(Runnable::run);
			}
		} catch (RuntimeException ex)
		{
			LOGGER.log(Level.WARNING, "Exception when running repeated tasks.", ex);
		}
	}

}
