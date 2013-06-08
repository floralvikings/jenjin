package com.jenjinstudios.jgsf;

import com.jenjinstudios.io.BaseMessage;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ExecutableMessage} class should be extended to create self-handling messages on the server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public abstract class ExecutableMessage implements Cloneable
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutableMessage.class.getName());
	/** The reflections object used for finding classes. */
	private static final Reflections reflections = new Reflections();
	/** The collection of ExecutableMessage classes. */
	private static final Set<Class<? extends ExecutableMessage>> executableMessageClasses = reflections
			.getSubTypesOf(ExecutableMessage.class);
	/** The collection of constructors. */
	private static final TreeMap<String, Constructor<? extends ExecutableMessage>> constructors = getConstructors();

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ExecutableMessage(ClientHandler handler, BaseMessage message)
	{
	}

	/** Run the synced portion of this message. */
	public abstract void runSynced();

	/** Run asynchronous portion of this message. */
	public abstract void runASync();

	/**
	 * Get a tree map of constructors for each ExecutableMessage.
	 *
	 * @return A tree map of constructors for each ExecutalMessage.
	 */
	@SuppressWarnings("unchecked")
	private static TreeMap<String, Constructor<? extends ExecutableMessage>> getConstructors()
	{
		TreeMap<String, Constructor<? extends ExecutableMessage>> constructors = new TreeMap<>();

		for (Class<? extends ExecutableMessage> c : executableMessageClasses)
			try
			{
				Constructor<? extends ExecutableMessage> current;
				// Have to suppress this warning because Java is dumb.
				current = (Constructor<? extends ExecutableMessage>) c.getConstructors()[0];
				constructors.put(current.getParameterTypes()[1].getName(), current);
			} catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Incorrect Constructor for class: " + c.getName(), e);
			}

		return constructors;
	}

	/**
	 * Get the class of the ExecutableMessage that handles the given BaseMessage.
	 *
	 * @param handler The client handler to use the ExecutableMessage.
	 * @param message The message.
	 * @return The class of the ExecutableMessage that handles the given BaseMessage.
	 */
	public static ExecutableMessage getExecutableMessageFor(ClientHandler handler, BaseMessage message)
	{
		ExecutableMessage r = null;
		String messageClassName = message.getClass().getName();
		Constructor<? extends ExecutableMessage> constructor = constructors.get(messageClassName);
		if (constructor != null)
			try
			{
				r = constructor.newInstance(handler, message);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
			{
				LOGGER.log(Level.SEVERE, "Incorrect Constructor Found For Class: " + constructor.getName(), e);
			}
		return r;
	}
}
