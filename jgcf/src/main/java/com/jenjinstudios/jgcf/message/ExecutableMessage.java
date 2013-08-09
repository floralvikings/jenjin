package com.jenjinstudios.jgcf.message;

/**
 * The {@code ExecutableMessage} class should be extended to create self-handling messages on the server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public abstract class ExecutableMessage implements Runnable
{
	/** The BaseMessage for this ExecutableMessage. */
	private final BaseMessage message;

	/**
	 * Construct an ExecutableMessage with the given BaseMessage.
	 *
	 * @param message The BaseMessage.
	 */
	protected ExecutableMessage(BaseMessage message)
	{
		if (!getClass().isAssignableFrom(MessageRegistry.getMessageType(message.getID()).executableMessageClass))
			throw new IllegalArgumentException("BaseMessage supplied to " + getClass().getName() + "is invalid.");

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
	 * The BaseMessage for this ExecutableMessage.
	 *
	 * @return The BaseMessage used by this ExecutableMessage
	 */
	public BaseMessage getMessage()
	{
		return message;
	}
}
