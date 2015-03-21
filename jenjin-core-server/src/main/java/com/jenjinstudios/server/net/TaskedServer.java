package com.jenjinstudios.server.net;

import com.jenjinstudios.server.authentication.Authenticator;

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
	private final Authenticator authenticator;
	private final List<Runnable> repeatedTasks;
	private final Deque<Runnable> syncedTasks;
	private ScheduledExecutorService loopTimer;
	private ServerUpdateTask serverUpdateTask;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @throws java.io.IOException If there is an IO Error initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler
	 * constructor.
	 */
	public TaskedServer(ServerInit initInfo, Authenticator authenticator) throws IOException,
		  NoSuchMethodException {
		super(initInfo);
		repeatedTasks = new LinkedList<>();
		syncedTasks = new LinkedList<>();
		this.authenticator = authenticator;
	}

	/**
	 * Get the start time, in nanoseconds, of the current update cycle.
	 * @return The cycle start time.
	 */
	public long getCycleStartTime() {
		return (serverUpdateTask != null) ? serverUpdateTask.getCycleStartTime() : -1;
	}

	/**
	 * The SQLHandler used by this Server.
	 *
	 * @return The SQLHandler used by this Server.
	 */
	public Authenticator getAuthenticator() { return authenticator; }

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

	public int getUps() { return UPS; }

	public void runRepeatedTasks() {
		synchronized (repeatedTasks)
		{
			repeatedTasks.forEach(Runnable::run);
		}
	}

	public void runSyncedTasks() {
		synchronized (syncedTasks)
		{
			while (!syncedTasks.isEmpty()) { syncedTasks.remove().run(); }
		}
	}

}
