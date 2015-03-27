package com.jenjinstudios.server.net;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the update loop in the form of a TimerTask.
 * @author Caleb Brinkman
 */
public class ServerUpdateTask implements Runnable
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ServerUpdateTask.class.getName());
	private final List<Runnable> repeatedTasks;
	private final Deque<Runnable> syncedTasks;
	public static final double MILLIS_IN_SECOND = 1000d;
	/** The time in nanoseconds of the last 50 update cycles. */
	private final double[] lastCycles;
	/** The server for which this loop runs. */
	private final Server server;
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
	public ServerUpdateTask(Server server) {
		this.server = server;
		lastCycles = new double[server.getUps() * 10];
		cycleNum = 0;
		repeatedTasks = new LinkedList<>();
		syncedTasks = new LinkedList<>();
	}

	@Override
	public void run() {
		startNewCycle();
		checkForNewClients();
		runSynchronizedTasks();
		runRepeatedTasks();
		update();
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

	private void update() {
		try
		{
			server.update();
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Exception when updating server", ex);
		}
	}

	/** Run the repeated tasks in the server queue. */
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

	/** Run the synchronized tasks in the server queue. */
	private void runSynchronizedTasks() {
		try
		{
			synchronized (syncedTasks)
			{
				while (!syncedTasks.isEmpty()) { syncedTasks.remove().run(); }
			}
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Exception when running repeated tasks.", ex);
		}
	}

	/** Called at the beginning of a new server update cycle. */
	private void startNewCycle() {
		long oldCycleStart = cycleStart == 0 ? System.currentTimeMillis() - 1000 / server.getUps() : cycleStart;
		cycleStart = System.currentTimeMillis();
		double cycleLength = (cycleStart - oldCycleStart) / MILLIS_IN_SECOND;
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

	public void addRepeatedTask(Runnable r) {
		synchronized (repeatedTasks)
		{
			repeatedTasks.add(r);
		}
	}

	private int getCycleArrayIndex() { return Math.abs(cycleNum) % lastCycles.length; }

	double getAverageUPS() { return averageUPS; }

	public long getCycleStartTime() { return cycleStart; }
}
