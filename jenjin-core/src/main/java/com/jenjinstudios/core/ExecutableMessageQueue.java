package com.jenjinstudios.core;

import java.util.Collection;
import java.util.LinkedList;

/**
 * This class is used to store and execute ExecutableMessages.
 *
 * @author Caleb Brinkman
 */
public class ExecutableMessageQueue
{
    private final Collection<ExecutableMessage> queuedExecutableMessages = new LinkedList<>();

    /**
     * Add an {@code ExecutableMessage} to the end of the queue.
     *
     * @param executableMessage The {@code ExecutableMessage} to add.
     */
    protected void queueExecutableMessage(ExecutableMessage executableMessage) {
        synchronized (queuedExecutableMessages)
        {
            queuedExecutableMessages.add(executableMessage);
        }
    }

    /**
     * Execute the {@code runDelayed} method of each {@code ExecutableMessage} in the queue, in the order in which they
     * were added.  After this method is called, the queue will be empty.
     */
    // TODO Maybe should separate the clear operation out of this method?
    public void runQueuedExecutableMessages() {
        synchronized (queuedExecutableMessages)
        {
            for (ExecutableMessage executableMessage : queuedExecutableMessages)
                executableMessage.runDelayed();
            queuedExecutableMessages.clear();
        }
    }
}
