package com.jenjinstudios.server.net;

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
		lastCycles = new double[server.getUps() * 10];
		cycleNum = 0;
	}

	@Override
	public void run() {
		startNewCycle();
		checkForNewClients();
		runSynchronizedTasks();
		runRepeatedTasks();
		runQueuedMessages();
		update();
		broadcast();
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

	private void broadcast() {
		try
		{
			server.broadcast();
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Exception when broadcasting", ex);
		}
	}

	private void update() {
		try
		{
			server.update();
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Exception when updating server", ex);
		}
	}

	private void runQueuedMessages() {
		try
		{
			server.runClientHandlerQueuedMessages();
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Exception when executing client messages", ex);
		}
	}

	/** Run the repeated tasks in the server queue. */
	private void runRepeatedTasks() {
		try
		{
			server.runRepeatedTasks();
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Exception when running repeated tasks.", ex);
		}
	}

	/** Run the synchronized tasks in the server queue. */
	private void runSynchronizedTasks() {
		try
		{
			server.runSyncedTasks();
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Exception when running repeated tasks.", ex);
		}
	}

	/** Called at the beginning of a new server update cycle. */
	private void startNewCycle() {
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
