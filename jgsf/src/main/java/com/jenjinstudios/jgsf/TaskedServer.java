package com.jenjinstudios.jgsf;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Timer;

/**
 * A Server responsible for scheduling and running tasks.
 *
 * @author Caleb Brinkman
 */
public class TaskedServer<T extends ClientHandler> extends Server<T>
{
	/** Tasks to be repeated in the main loop. */
	private final LinkedList<Runnable> repeatedTasks;
	/** Synced tasks scheduled by client handlers. */
	private final LinkedList<Runnable> syncedTasks;
	/** The timer that controls the server loop. */
	private Timer loopTimer;
	/** The server loop. */
	private ServerLoop serverLoop;

	/**
	 * Construct a new SqlEnabledServer without a SQLHandler.
	 *
	 * @param ups          The cycles per second at which this server will run.
	 * @param port         The port number on which this server will listen.
	 * @param handlerClass The class of ClientHandler used by this SqlEnabledServer.
	 *
	 * @throws java.sql.SQLException If there's a SQL exception.
	 */
	public TaskedServer(int ups, int port, Class<? extends T> handlerClass) throws SQLException
	{
		super(ups, port, handlerClass);
		repeatedTasks = new LinkedList<>();
		syncedTasks = new LinkedList<>();
	}

	/**
	 * Get the start time, in nanoseconds, of the current update cycle.
	 *
	 * @return The cycle start time.
	 */
	public long getCycleStartTime()
	{
		return serverLoop != null ? serverLoop.getCycleStart() : -1;
	}

	/**
	 * Add a task to be repeated every update.
	 *
	 * @param r The {@code Runnable} containing the task to be repeated.
	 */
	public void addRepeatedTask(Runnable r)
	{
		synchronized (repeatedTasks)
		{
			repeatedTasks.add(r);
		}
	}

	/**
	 * Add an ExecutableMessage to the synced tasks list.
	 *
	 * @param r The {@code ExecutableMessage} to add.
	 */
	public void addSyncedTask(Runnable r)
	{
		synchronized (syncedTasks)
		{
			syncedTasks.add(r);
		}
	}

	@Override
	public void run()
	{
		super.run();

		serverLoop = new ServerLoop(this);

		/* The name of the timer that is looping the server thread. */
		String timerName = "SqlEnabledServer Update Loop";
		loopTimer = new Timer(timerName, false);
		loopTimer.scheduleAtFixedRate(serverLoop, 0, PERIOD);
	}

	/**
	 * Shut down the server.
	 *
	 * @throws IOException If there's an IOException.
	 */
	public void shutdown() throws IOException
	{
		super.shutdown();

		if (loopTimer != null)
			loopTimer.cancel();
	}

	/**
	 * The actual average UPS of this server.
	 *
	 * @return The average UPS of this server
	 */
	public double getAverageUPS()
	{
		return serverLoop.getAverageUPS();
	}

	/**
	 * Tasks to be repeated in the main loop.
	 *
	 * @return The list of repeated tasks to be executed by this server.
	 */
	LinkedList<Runnable> getRepeatedTasks()
	{
		return repeatedTasks;
	}

	/**
	 * Synced tasks scheduled by client handlers.
	 *
	 * @return The list of syncrhonized tasks scheduled by ClientHandlers.
	 */
	LinkedList<Runnable> getSyncedTasks()
	{
		return syncedTasks;
	}
}
