package com.jenjinstudios.core;

import com.jenjinstudios.core.message.ExecutableMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Caleb Brinkman
 */
public class ExecutableMessageQueue
{
	/** The "one-shot" tasks to be executed in the current client loop. */
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
