package com.jenjinstudios.core.io;

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
	public void runDelayed() {
		throw new IllegalStateException("This message should be disabled.");
	}

	@Override
	public void runImmediate() {
		throw new IllegalStateException("This message should be disabled.");
	}
}
