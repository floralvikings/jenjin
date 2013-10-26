package com.jenjinstudios.net;

import com.jenjinstudios.message.ExecutableMessage;
import com.jenjinstudios.message.Message;

import java.util.LinkedList;

/**
 * The communicator class is the superclass for any classes that communicate over socket.
 *
 * @author Caleb Brinkman
 */
public abstract class TaskedCommunicator extends Communicator
{
	/** The "one-shot" tasks to be executed in the current client loop. */
	private final LinkedList<Runnable> syncedTasks;

	/** Cosntruct a new Commuicator. */
	protected TaskedCommunicator()
	{
		syncedTasks = new LinkedList<>();
	}

	/**
	 * Process the specified message.  This method should be overridden by any implementing classes, but it does contain
	 * functionality necessary to communicate with a DownloadServer or a ChatServer.
	 *
	 * @param message The message to be processed.
	 */
	protected void processMessage(Message message)
	{
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
	 * Get an executable message for a given message.
	 *
	 * @param message The message to be used.
	 *
	 * @return The ExecutableMessage.
	 */
	protected abstract ExecutableMessage getExecutableMessage(Message message);

	/**
	 * The "one-shot" tasks to be executed in the current client loop.
	 *
	 * @return The list of Synced Tasks
	 */
	public LinkedList<Runnable> getSyncedTasks()
	{
		LinkedList<Runnable> temp = new LinkedList<>();
		synchronized (syncedTasks)
		{
			temp.addAll(syncedTasks);
			syncedTasks.removeAll(temp);
		}
		return temp;
	}
}
