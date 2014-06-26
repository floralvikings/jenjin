package com.jenjinstudios.server.net;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

/**
 * A server which executes runnable tasks.
 * @author Caleb Brinkman
 */
public class TaskedServer<T extends ClientHandler> extends Server<T>
{
	/** Tasks to be repeated in the main loop. */
	private final List<Runnable> repeatedTasks;
	/** Synced tasks scheduled by client handlers. */
	private final LinkedList<Runnable> syncedTasks;
	/** The timer that controls the server loop. */
	private Timer loopTimer;
	/** The server loop. */
	private ServerUpdateTask serverUpdateTask;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @throws java.io.IOException If there is an IO Error initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler
	 * constructor.
	 */
	public TaskedServer(ServerInit<T> initInfo) throws IOException, NoSuchMethodException {
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
	public void addRepeatedTask(Runnable r) {
		synchronized (repeatedTasks)
		{
			repeatedTasks.add(r);
		}
	}

	@Override
	public void run() {
		super.run();

		serverUpdateTask = new ServerUpdateTask(this);

		/* The name of the timer that is looping the server thread. */
		String timerName = "Server Update Loop";
		loopTimer = new Timer(timerName, false);
		loopTimer.scheduleAtFixedRate(serverUpdateTask, 0, PERIOD);
	}

	@Override
	public void shutdown() throws IOException {
		super.shutdown();

		if (loopTimer != null)
			loopTimer.cancel();
	}

	public double getAverageUPS() { return serverUpdateTask.getAverageUPS(); }

	public int getUps() { return UPS; }

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
