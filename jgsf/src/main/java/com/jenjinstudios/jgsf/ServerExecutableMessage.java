package com.jenjinstudios.jgsf;

import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.message.BaseMessage;
import com.jenjinstudios.message.ExecutableMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ServerExecutableMessage class is invoked to respond to messages server-side.
 *
 * @author Caleb Brinkman
 */
public abstract class ServerExecutableMessage extends ExecutableMessage
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ServerExecutableMessage.class.getName());
	/** The ClientHandler for this object. */
	private static ClientHandler clientHandler;


	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ServerExecutableMessage(ClientHandler handler, BaseMessage message)
	{
		super(message);
		clientHandler = handler;
	}

	/**
	 * Get the class of the ExecutableMessage that handles the given BaseMessage.
	 *
	 * @param handler The client handler to use the ExecutableMessage.
	 * @param message The message.
	 * @return The class of the ExecutableMessage that handles the given BaseMessage.
	 */
	@SuppressWarnings("unchecked")
	public static ExecutableMessage getServerExecutableMessageFor(ClientHandler handler, BaseMessage message)
	{
		ExecutableMessage r = null;
		if (!ExecutableMessage.areMessagesRegistered()) registerMessages();
		if (!MessageRegistry.hasMessagesRegistered()) MessageRegistry.registerXmlMessages();

		Class<? extends ExecutableMessage> execClass = executableMessageClasses.get(message.getID());

		try
		{
			Constructor<? extends ExecutableMessage>[] execConstructors;
			Constructor<? extends ExecutableMessage> execConstructor = null;
			execConstructors = (Constructor<? extends ExecutableMessage>[]) execClass.getConstructors();
			for (Constructor<? extends ExecutableMessage> constructor : execConstructors)
			{
				// Check to see if the first argument is a ClientHandler
				if (ClientHandler.class.isAssignableFrom(constructor.getParameterTypes()[0]))
					execConstructor = constructor;
			}
			if (execConstructor != null)
			{
				r = execConstructor.newInstance(handler, message);
			} else
			{
				LOGGER.log(Level.SEVERE, "No constructor containing ClientHandler as first argument type found for {0}",
						execClass.getName());
			}
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e)
		{
			LOGGER.log(Level.SEVERE, "Constructor not correct for: " + execClass.getName(), e);
		}

		return r;
	}

	/**
	 * Get the ClientHandler invoking this message.
	 *
	 * @return The ClientHandler invoking this message.
	 */
	public static ClientHandler getClientHandler()
	{
		return clientHandler;
	}
}
