package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.xml.MessageType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ExecutableMessage} class is one of the most important classes of the Jenjin dynamic messaging system.  A
 * subclass of the {@code ExecutableMessage} class is constructed reflectively by a {@code Connection} when it received
 * a message, based on information in the {@code MessageRegistry}.  After constructing the message, the {@code
 * runImmediate} method is (immediately) invoked.  After it finishes executing, the {@code ExecutableMessage} is added
 * to a queue in the {@code Connection}'s execution timer, and is later executed in a synchronous fashion with the other
 * events in the connection's execution timer.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public abstract class ExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableMessage.class.getName());
	private final Message message;

	/**
	 * Construct a new ExecutableMessage; this should only ever be invoked reflectively, by a {@code Connection}'s
	 * update cycle.
	 *
	 * @param message The message that caused this {@code ExecutableMessage} to be created.
	 */
	protected ExecutableMessage(Message message) {
		this.message = message;
	}

	/**
	 * Given a {@code Connection} and a {@code Message}, create and return an appropriate {@code ExecutableMessage}.
	 *
	 * @param connection The {@code Connection} creating this {@code ExecutableMessage}.
	 * @param message The {@code Message} for which the {@code ExecutableMessage} is being created.
	 *
	 * @return The {@code ExecutableMessage} created for {@code connection} and {@code message}.
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
		String className = messageType.getExecutable();
		Constructor[] execConstructors = new Constructor[0];
		try
		{
			Class execClass = Class.forName(className);
			execConstructors = execClass.getConstructors();
		} catch (ClassNotFoundException | NullPointerException e)
		{
			LOGGER.log(Level.WARNING, "Could not find class: {0}", className);
		}
		return getAppropriateConstructor(connection, execConstructors);
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

	public abstract void runDelayed();

	public abstract void runImmediate();

	public Message getMessage() {
		return message;
	}
}
