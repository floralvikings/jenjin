package com.jenjinstudios.server.net;

import com.jenjinstudios.server.authentication.Authenticator;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskedServer extends Server
{
	private final Deque<Runnable> syncedTasks;
	private ScheduledExecutorService loopTimer;
	private ServerUpdateTask serverUpdateTask;

	public TaskedServer(ServerInit initInfo, Authenticator authenticator) throws IOException,
		  NoSuchMethodException {
		super(initInfo, authenticator);
		syncedTasks = new LinkedList<>();
	}

	public long getCycleStartTime() {
		return (serverUpdateTask != null) ? serverUpdateTask.getCycleStartTime() : -1;
	}

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

	public void runSyncedTasks() {
		synchronized (syncedTasks)
		{
			while (!syncedTasks.isEmpty()) { syncedTasks.remove().run(); }
		}
	}

}
