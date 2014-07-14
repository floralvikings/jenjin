package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ExecutableMessage} class should be extended to create self-handling messages on the server.
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public abstract class ExecutableMessage
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutableMessage.class.getName());
	/** The Message for this ExecutableMessage. */
	private final Message message;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param message The Message.
	 */
	protected ExecutableMessage(Message message) {
		this.message = message;
	}

	/** Run the synced portion of this message. */
	public abstract void runDelayed();

	/** Run asynchronous portion of this message. */
	public abstract void runImmediate();

	/**
	 * The Message for this ExecutableMessage.
	 * @return The Message used by this ExecutableMessage
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * Get an executable message for the given connection and message.
	 * @param connection The connection.
	 * @param message The Message.
	 * @return The ExecutableMessage appropriate to the given message.
	 */
	@SuppressWarnings("unchecked")
	public static ExecutableMessage getExecutableMessageFor(Connection connection, Message message) {
		ExecutableMessage executableMessage = null;

		MessageType messageType = connection.getMessageRegistry().getMessageType(message.getID());
		List<Class<? extends ExecutableMessage>> execClasses = messageType.getExecutableMessageClasses();

		try
		{
			Constructor<? extends ExecutableMessage> execConstructor = null;
			for (Class<? extends ExecutableMessage> execClass : execClasses)
			{
				if (execClass == null) continue;
				Constructor<? extends ExecutableMessage>[] execConstructors;
				execConstructors = (Constructor<? extends ExecutableMessage>[]) execClass.getConstructors();
				execConstructor = getAppropriateConstructor(connection, execConstructors);
			}
			if (execConstructor != null)
			{
				executableMessage = execConstructor.newInstance(connection, message);
			} else
			{
				LOGGER.log(Level.SEVERE, "No constructor containing Connection or {0} as first argument type found for {1}",
						new Object[]{connection.getClass().getName(), message.name});
			}
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e)
		{
			LOGGER.log(Level.SEVERE, "Constructor not correct", e);
		} catch (NullPointerException e)
		{
			LOGGER.log(Level.SEVERE, "No executable message found for: " + message, e);
		}

		return executableMessage;
	}

	private static Constructor getAppropriateConstructor(Connection connection, Constructor[] execConstructors) {
		// TODO Make this more specific.
		Constructor correctConstructor = null;
		for (Constructor constructor : execConstructors)
		{
			Class<?> firstParam = constructor.getParameterTypes()[0];
			if (firstParam.isAssignableFrom(connection.getClass()))
				correctConstructor = constructor;
		}
		return correctConstructor;
	}
}
