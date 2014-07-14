package com.jenjinstudios.server.net;

import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the update loop in the form of a TimerTask.
 * @author Caleb Brinkman
 */
class ServerUpdateTask implements Runnable
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ServerUpdateTask.class.getName());
	/** The time in nanoseconds of the last 50 update cycles. */
	private final double[] lastCycles;
	/** The server for which this loop runs. */
	private final TaskedServer server;
	/** The list of synchronized tasks to be executed by the loop. */
	private final Deque<Runnable> syncedTasks;
	/** The list of repeated tasks to be executed by the loop. */
	private final Iterable<Runnable> repeatedTasks;
	/** The start time, in nanoseconds, of the current cycle. */
	private volatile long cycleStart = 0;
	/** The current cycle number. */
	private int cycleNum;
	/** The actual average UPS of this server. */
	private double averageUPS;

	/**
	 * Construct a new {@code ServerLoop} for the specified server.
	 * @param server The server for which this server loop works.
	 */
	@SuppressWarnings("unchecked")
	public ServerUpdateTask(TaskedServer server) {
		this.server = server;
		syncedTasks = this.server.getSyncedTasks();
		repeatedTasks = this.server.getRepeatedTasks();
		lastCycles = new double[server.getUps() * 10];
		cycleNum = 0;
	}

	@Override
	public void run() {
		newCycle();
		boolean clientsAdded = server.getNewClients();
		if (clientsAdded) LOGGER.log(Level.FINE, "New Clients Added");
		runSynchronizedTasks();
		runRepeatedTasks();
		server.runClientHandlerQueuedMessages();
		server.update();
		server.broadcast();
		server.refresh();
	}

	/** Run the repeated tasks in the server queue. */
	private void runRepeatedTasks() {
		synchronized (repeatedTasks)
		{
			for (Runnable r : repeatedTasks) { r.run(); }
		}
	}

	/** Run the synchronized tasks in the server queue. */
	private void runSynchronizedTasks() {
		synchronized (syncedTasks)
		{
			while (!syncedTasks.isEmpty()) { syncedTasks.remove().run(); }
		}
	}

	/** Called at the beginning of a new server update cycle. */
	private void newCycle() {
		long oldCycleStart = cycleStart == 0 ? System.currentTimeMillis() - 1000 / server.getUps() : cycleStart;
		cycleStart = System.currentTimeMillis();
		double cycleLength = (cycleStart - oldCycleStart) / 1000d;
		lastCycles[getCycleArrayIndex()] = cycleLength;
		cycleNum++;
		double total = 0;
		for (double l : lastCycles)
		{
			total += l;
		}
		averageUPS = (ceilNumCycles() / total);
	}

	private double ceilNumCycles() {
		return cycleNum < 1 || cycleNum > lastCycles.length ?
			  lastCycles.length : cycleNum;
	}

	private int getCycleArrayIndex() { return Math.abs(cycleNum) % lastCycles.length; }

	double getAverageUPS() { return averageUPS; }

	long getCycleStartTime() { return cycleStart; }
}
