package com.jenjinstudios.core;

import com.jenjinstudios.core.message.ExecutableMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to store and execute ExecutableMessages.
 *
 * @author Caleb Brinkman
 */
public class ExecutableMessageQueue
{
	private final List<ExecutableMessage> queuedExecutableMessages;

	public ExecutableMessageQueue() {
		queuedExecutableMessages = new LinkedList<>();
	}

	protected void queueExecutableMessage(ExecutableMessage executableMessage) {
		synchronized (queuedExecutableMessages)
		{
			queuedExecutableMessages.add(executableMessage);
		}
	}

	protected void runQueuedExecutableMessages() {
		synchronized (queuedExecutableMessages)
		{
			for (ExecutableMessage executableMessage : queuedExecutableMessages)
				executableMessage.runDelayed();
			queuedExecutableMessages.clear();
		}
	}
}
