package com.jenjinstudios.message;

/**
 * The {@code ExecutableMessage} class should be extended to create self-handling messages on the server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public abstract class ExecutableMessage implements Runnable
{
	/** The Message for this ExecutableMessage. */
	private final Message message;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param message The Message.
	 */
	protected ExecutableMessage(Message message)
	{
		this.message = message;
	}

	/** Run the synced portion of this message. */
	public abstract void runSynced();

	/** Run asynchronous portion of this message. */
	public abstract void runASync();

	/** Calls the {@code runSynced} method. */
	public final void run()
	{
		runSynced();
	}

	/**
	 * The Message for this ExecutableMessage.
	 *
	 * @return The Message used by this ExecutableMessage
	 */
	public Message getMessage()
	{
		return message;
	}
}
