package com.jenjinstudios.server.net;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A server which executes runnable tasks.
 * @author Caleb Brinkman
 */
public class TaskedServer extends Server
{
	/** Tasks to be repeated in the main loop. */
	private final List<Runnable> repeatedTasks;
	/** Synced tasks scheduled by client handlers. */
	private final Deque<Runnable> syncedTasks;
	/** The timer that controls the server loop. */
	private ScheduledExecutorService loopTimer;
	/** The server loop. */
	private ServerUpdateTask serverUpdateTask;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @throws java.io.IOException If there is an IO Error initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler
	 * constructor.
	 */
	protected TaskedServer(ServerInit initInfo) throws IOException, NoSuchMethodException {
		super(initInfo);
		repeatedTasks = new LinkedList<>();
		syncedTasks = new LinkedList<>();
	}

	/**
	 * Get the start time, in nanoseconds, of the current update cycle.
	 * @return The cycle start time.
	 */
	public long getCycleStartTime() {
		return serverUpdateTask != null ? serverUpdateTask.getCycleStartTime() : -1;
	}

	/**
	 * Add a task to be repeated every update.
	 * @param r The {@code Runnable} containing the task to be repeated.
	 */
	protected void addRepeatedTask(Runnable r) {
		synchronized (repeatedTasks)
		{
			repeatedTasks.add(r);
		}
	}

	@Override
	public void run() {
		super.run();

		serverUpdateTask = new ServerUpdateTask(this);

		loopTimer = Executors.newSingleThreadScheduledExecutor(new ServerUpdateThreadFactory());
		loopTimer.scheduleAtFixedRate(serverUpdateTask, 0, PERIOD, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws IOException {
		super.shutdown();

		if (loopTimer != null)
			loopTimer.shutdown();
	}

	public double getAverageUPS() { return serverUpdateTask.getAverageUPS(); }

	public int getUps() { return UPS; }

	public void runRepeatedTasks() {
		synchronized (repeatedTasks)
		{
			for (Runnable r : repeatedTasks) r.run();
		}
	}

	public void runSyncedTasks() {
		synchronized (syncedTasks)
		{
			while (!syncedTasks.isEmpty()) { syncedTasks.remove().run(); }
		}
	}

	/**
	 * Tasks to be repeated in the main loop.
	 * @return The list of repeated tasks to be executed by this server.
	 */
	Iterable<Runnable> getRepeatedTasks() { return repeatedTasks; }

	/**
	 * Synced tasks scheduled by client handlers.
	 * @return The list of synchronized tasks scheduled by ClientHandlers.
	 */
	Deque<Runnable> getSyncedTasks() { return syncedTasks; }
}
