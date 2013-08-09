package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.Client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Superclass of ExecutableMessages used by the client.
 *
 * @author Caleb Brinkman
 */
public abstract class ClientExecutableMessage extends ExecutableMessage
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutableMessage.class.getName());
	/** The client invoking this ExecutableMessage. */
	private final Client client;

	/**
	 * Construct an ExecutableMessage with the given BaseMessage.
	 *
	 * @param client The client invoking this message.
	 * @param message The BaseMessage.
	 */
	protected ClientExecutableMessage(Client client, BaseMessage message)
	{
		super(message);
		this.client = client;
	}

	/**
	 * Get the class of the ExecutableMessage that handles the given BaseMessage.
	 *
	 * @param client  The client invoking the message.
	 * @param message The message.
	 * @return The class of the ExecutableMessage that handles the given BaseMessage.
	 */
	@SuppressWarnings("unchecked")
	public static ExecutableMessage getClientExecutableMessageFor(Client client, BaseMessage message)
	{
		ExecutableMessage r = null;

		MessageType messageType = MessageRegistry.getMessageType(message.getID());
		Class<? extends ExecutableMessage> execClass = messageType.executableMessageClass;

		try
		{
			Constructor<? extends ExecutableMessage>[] execConstructors;
			Constructor<? extends ExecutableMessage> execConstructor = null;
			execConstructors = (Constructor<? extends ExecutableMessage>[]) execClass.getConstructors();
			for (Constructor<? extends ExecutableMessage> constructor : execConstructors)
			{
				// Check to see if the first argument is a Client
				if (Client.class.isAssignableFrom(constructor.getParameterTypes()[0]))
					execConstructor = constructor;
			}
			if (execConstructor != null)
			{
				r = execConstructor.newInstance(client, message);
			} else
			{
				LOGGER.log(Level.SEVERE, "No constructor containing Client as first argument type found for {0}",
						execClass.getName());
			}
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e)
		{
			LOGGER.log(Level.SEVERE, "Constructor not correct for: " + execClass.getName(), e);
		}

		return r;
	}

	/**
	 * Get the client invoking this ExecutableMessage.
	 *
	 * @return The client invoking this ExecutableMessage.
	 */
	public Client getClient()
	{
		return client;
	}
}
