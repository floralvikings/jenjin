package com.jenjinstudios.message;

import com.jenjinstudios.io.BaseMessage;
import org.reflections.Reflections;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the registration of message classes and the information on how to reconstruct them from raw data.
 *
 * @author Caleb Brinkman
 */
public class MessageRegistry
{
	/** The logger for this class. */
	public static final Logger LOGGER = Logger.getLogger(MessageRegistry.class.getName());
	/** The collection of message class names. */
	private static final TreeMap<Short, String> classRegistry = new TreeMap<>();
	/** The collection of message argument class names. */
	private static final TreeMap<Short, LinkedList<String>> fieldRegistry = new TreeMap<>();
	/** Flags whether messages have been registered. */
	private static boolean messagesRegistered;
	/** The reflections object used to search for BaseMessages. */
	private static Reflections reflections = new Reflections();

	/**
	 * Register the given class as a new message type.
	 *
	 * @param messageClass The class to register.
	 */
	private static void registerMessage(Class<? extends BaseMessage> messageClass)
	{
		Short ID;

		try
		{
			ID = (short) messageClass.getField("ID").get(null);
		} catch (IllegalAccessException | NoSuchFieldException e)
		{
			LOGGER.log(Level.SEVERE, " No public ID field for Class: " + messageClass.getName(), e);
			return;
		}

		if (classRegistry.containsKey(ID))
		{
			LOGGER.log(Level.FINE, "Message Registry already contains key: " + ID + ", " + messageClass.toString());
			return;
		}

		classRegistry.put(ID, messageClass.getName());

		LinkedList<String> fieldClasses;
		fieldClasses = new LinkedList<>();
		for (Class c : messageClass.getConstructors()[0].getParameterTypes()) fieldClasses.add(c.getName());
		fieldRegistry.put(ID, fieldClasses);
	}

	/** Register every class that extends BaseMessage.  Also registers the BaseMessage class. */
	public static void registerAllBaseMessages()
	{
		Set<Class<? extends BaseMessage>> baseMessageClasses;
		baseMessageClasses = reflections.getSubTypesOf(BaseMessage.class);
		for (Class<? extends BaseMessage> c : baseMessageClasses) registerMessage(c);
		messagesRegistered = true;
	}

	/**
	 * Get the class names of arguments for the class with the given registration ID.
	 *
	 * @param id The ID to lookup.
	 * @return A LinkedList of class names.
	 */
	public static LinkedList<String> getClassNames(short id)
	{
		LinkedList<String> temp = new LinkedList<>();
		synchronized (fieldRegistry)
		{
			temp.addAll(fieldRegistry.get(id));
		}
		return temp;
	}

	/**
	 * Get the class registered for the given ID.
	 *
	 * @param id The ID to lookup.
	 * @return The name of the class stored for the ID.
	 */
	public static String getMessageClass(short id)
	{
		synchronized (classRegistry)
		{
			if (!classRegistry.containsKey(id))
				throw new IllegalArgumentException("Message registry does not contain that message ID: " + id);
			return classRegistry.get(id);
		}
	}

	/**
	 * Get whether messages have been registered.
	 *
	 * @return Whether messages have been registered.
	 */
	public static boolean hasMessagesRegistered()
	{
		return messagesRegistered;
	}
}
