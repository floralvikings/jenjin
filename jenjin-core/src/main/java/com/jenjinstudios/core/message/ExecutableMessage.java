package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
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

	/**
	 * Get an executable message for the given connection and message.
	 * @param connection The connection.
	 * @param message The Message.
	 * @return The ExecutableMessage appropriate to the given message.
	 */
	public static ExecutableMessage getExecutableMessageFor(Connection connection, Message message) {
		ExecutableMessage executableMessage = null;
		Constructor execConstructor = getExecConstructor(connection, message);
		if (execConstructor != null)
		{
			executableMessage = createExec(connection, message, execConstructor);
		} else
		{
			Object[] args = {connection.getClass().getName(), message.name};
			String report = "No constructor containing Connection or {0} as first argument type found for {1}";
			LOGGER.log(Level.SEVERE, report, args);
		}
		return executableMessage;
	}

	private static Constructor getExecConstructor(Connection connection, Message message) {
		MessageType messageType = MessageRegistry.getInstance().getMessageType(message.getID());
		List<Class<? extends ExecutableMessage>> execClasses = messageType.getExecutableMessageClasses();

		Constructor execConstructor = null;
		for (Class<? extends ExecutableMessage> execClass : execClasses)
		{
			if (execClass == null) continue;
			Constructor[] execConstructors;
			execConstructors = execClass.getConstructors();
			execConstructor = getAppropriateConstructor(connection, execConstructors);
		}
		return execConstructor;
	}

	private static ExecutableMessage createExec(Connection conn, Message msg, Constructor constructor) {
		ExecutableMessage executableMessage = null;
		try
		{
			executableMessage = (ExecutableMessage) constructor.newInstance(conn, msg);
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e)
		{
			LOGGER.log(Level.SEVERE, "Constructor not correct", e);
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
}
