package com.jenjinstudios.net;

import com.jenjinstudios.message.ExecutableMessage;
import com.jenjinstudios.message.Message;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A communicator that executes runnable tasks.
 * @author Caleb Brinkman
 */
public abstract class TaskedCommunicator extends Communicator
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(TaskedCommunicator.class.getName());
	/** The "one-shot" tasks to be executed in the current client loop. */
	private final LinkedList<Runnable> syncedTasks;

	/** Construct a new TaskedCommunicator. */
	public TaskedCommunicator() {
		syncedTasks = new LinkedList<>();
	}

	/**
	 * Process the specified message.  This method should be overridden by any implementing classes, but it does contain
	 * functionality necessary to communicate with a DownloadServer or a ChatServer.
	 * @param message The message to be processed.
	 */
	protected void processMessage(Message message) {
		ExecutableMessage exec = getExecutableMessage(message);
		if (exec != null)
		{
			exec.runASync();
			synchronized (syncedTasks)
			{
				syncedTasks.add(exec);
			}
		} else
		{
			Message invalid = new Message("InvalidMessage");
			invalid.setArgument("messageName", message.name);
			invalid.setArgument("messageID", message.getID());
			queueMessage(invalid);
		}
	}

	/**
	 * The "one-shot" tasks to be executed in the current client loop.
	 * @return The list of Synced Tasks
	 */
	public LinkedList<Runnable> getSyncedTasks() {
		LinkedList<Runnable> temp = new LinkedList<>();
		synchronized (syncedTasks)
		{
			temp.addAll(syncedTasks);
			syncedTasks.removeAll(temp);
		}
		return temp;
	}

	@Override
	public void run() {
		super.run();
		try // TODO Make sure error is handled gracefully
		{
			Message currentMessage;
			while ((currentMessage = getInputStream().readMessage()) != null)
				processMessage(currentMessage);
		} catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Error retrieving message from server.", ex);
		} finally
		{
			shutdown();
		}
	}
}
