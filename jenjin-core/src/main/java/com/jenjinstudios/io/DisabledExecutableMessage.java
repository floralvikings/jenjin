package com.jenjinstudios.io;

/**
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessage extends ExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param message The Message.
	 */
	public DisabledExecutableMessage(Message message) {
		super(message);
	}

	@Override
	public void runSynced() {
		throw new IllegalStateException("This message should be disabled.");
	}

	@Override
	public void runASync() {
		throw new IllegalStateException("This message should be disabled.");
	}
}
