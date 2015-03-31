package com.jenjinstudios.core.concurrency;

import com.jenjinstudios.core.io.Message;

import java.util.logging.Logger;

/**
 * The ExecutableMessage class is central to the Jenjin message-processing system; using data in the global message
 * registry, a MessageExecutor thread constructs an Executable message based on each message received by a Connection.
 * Once the message is created, the execute method is immediately called on it.
 */
@SuppressWarnings("unused")
public abstract class ExecutableMessage<T extends MessageContext>
{
    private static final Logger LOGGER = Logger.getLogger(ExecutableMessage.class.getName());
    private final Message message;
	private final MessageThreadPool threadPool;
	private final T context;

	/**
	 * Construct a new ExecutableMessage; this should only ever be invoked reflectively, by a {@code Connection}'s
     * update cycle.
     *
	 * @param threadPool The threadPool for which this ExecutbleMessage will work.
	 * @param message The message that caused this {@code ExecutableMessage} to be created.
     */
	protected ExecutableMessage(MessageThreadPool threadPool, Message message) {
		this(threadPool, message, null);
	}

	protected ExecutableMessage(MessageThreadPool threadPool, Message message, T context) {
		this.message = message;
		this.threadPool = threadPool;
		this.context = context;
	}

	/**
	 * This method is invoked by a {@code MessageThreadPool} when a message is received and the {@code
	 * ExecutableMessage} is
	 * created, and should not be called directly; the returned Message is sent by the MessageThreadPool.
	 *
	 * @return The response to the received message, if any; null if no response should be made.
	 */
	public abstract Message execute();

    /**
     * Get the message for which this {@code ExecutableMessage} was created.
     *
     * @return The message for which this {@code ExecutableMessage} was created.
     */
	public Message getMessage() { return message; }

    /**
	 * Get the threadPool associated with this ExecutableMessage.
	 *
	 * @return The threadPool associated with this ExecutableMessage.
	 */
	public MessageThreadPool getThreadPool() { return threadPool; }

	/**
	 * Get the MessageContext for this executable.
	 *
	 * @return The MessageContext for this executable.
	 */
	public T getContext() { return context; }
}
