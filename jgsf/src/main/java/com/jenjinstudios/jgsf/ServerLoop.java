package com.jenjinstudios.jgsf;

import java.io.IOException;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the update loop in the form of a TimerTask.
 *
 * @author Caleb Brinkman
 */
class ServerLoop extends TimerTask
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ServerLoop.class.getName());
	/** The time in nanoseconds of the last 50 update cycles. */
	private final long[] lastCycles;
	/** The server for which this loop runs. */
	private final Server server;
	/** The list of synchronized tasks to be executed by the loop. */
	private final LinkedList<Runnable> syncedTasks;
	/** The list of repeated tasks to be executed by the loop. */
	private final LinkedList<Runnable> repeatedTasks;
	/** The start time, in nanoseconds, of the current cycle. */
	private volatile long cycleStart = 0;
	/** The current cycle number. */
	private long cycleNum;
	/** The actual average UPS of this server. */
	private double averageUPS;

	/**
	 * Construct a new {@code ServerLoop} for the specified server.
	 *
	 * @param server The server for which this server loop works.
	 */
	@SuppressWarnings("unchecked")
	public ServerLoop(Server server)
	{
		this.server = server;
		syncedTasks = this.server.getSyncedTasks();
		repeatedTasks = this.server.getRepeatedTasks();
		lastCycles = new long[server.UPS];
		cycleNum = 0;
	}

	/** Called at the beginning of a new server update cycle. */
	private void newCycle()
	{
		long oldCycleStart = cycleStart;
		cycleStart = System.nanoTime();
		cycleNum++;
		lastCycles[(int) cycleNum % lastCycles.length] = cycleStart - oldCycleStart;
		long total = 0;
		for (long l : lastCycles)
			total += l;
		double averageLength = (total / lastCycles.length);
		averageUPS = 1000000000 / averageLength;
	}

	/** Run the synchronized tasks in the server queue. */
	private void runSynchronizedTasks()
	{
		synchronized (syncedTasks)
		{
			while (!syncedTasks.isEmpty())
			{
				syncedTasks.remove().run();
			}
		}
	}

	/** Run the repeated tasks in the server queue. */
	private void runRepeatedTasks()
	{
		synchronized (repeatedTasks)
		{
			for (Runnable r : repeatedTasks)
			{
				r.run();
			}
		}
	}

	public void run()
	{
		newCycle();
		boolean clientsAdded = server.getNewClients();
		if (clientsAdded) LOGGER.log(Level.FINE, "New Clients Added");
		runSynchronizedTasks();
		runRepeatedTasks();
		server.update();
		try
		{
			server.broadcast();
		} catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Error broadcasting. ", e);
		}
		server.refresh();
	}

	/**
	 * The actual average UPS of this server.  Blocks until at least 50 server cycles have been completed.
	 *
	 * @return The average UPS for the past 50 updates.
	 */
	double getAverageUPS()
	{
		return averageUPS;
	}

	/**
	 * The start time, in nanoseconds, of the current cycle.
	 *
	 * @return The start time of the current cycle.
	 */
	long getCycleStart()
	{
		return cycleStart;
	}
}
