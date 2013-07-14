package com.jenjinstudios.message;

import com.jenjinstudios.io.MessageRegistry;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * The base for all message classes that can be registered for the JGC and JGSA.
 *
 * @author Caleb Brinkman
 */
public class BaseMessage implements Serializable
{
	/** The arguments to be passed to the message. */
	private final Object[] args;
	/** The ID used to register and create this message. */
	private final short ID;

	/**
	 * Construct a new message using the given ID and arguments.
	 *
	 * @param id The ID number of the message type.
	 * @param args The arguments used to create the message.
	 */
	public BaseMessage(short id, Object... args)
	{
		this.args = args;
		this.ID = id;
		if(isInvalid())
		{
			Class[] classes = new Class[args.length];
			for(int i=0; i< args.length; i++)
				classes[i] = args[i].getClass();
			throw new IllegalArgumentException("Incorrect arguments for BaseMessage ID: " + ID +
					", " + Arrays.toString(classes) + ", " + MessageRegistry.getArgumentClasses(ID));
		}
	}

	/**
	 * Get the ID of this message.
	 *
	 * @return The ID of this message
	 */
	public short getID()
	{
		return ID;
	}

	/**
	 * Get the arguments for this message.
	 *
	 * @return The arguments for this message.
	 */
	public final Object[] getArgs()
	{
		return args;
	}

	/**
	 * Determine if this message is invalid.
	 * @return Whether this message in invalid.
	 */
	private boolean isInvalid()
	{
		boolean invalid = false;
		LinkedList<Class> argumentClasses = MessageRegistry.getArgumentClasses(ID);
		// Must be the same number of arguments.
		if(args.length == argumentClasses.size())
		{
			for(int i=0; i<argumentClasses.size(); i++)
			{
				Class current = argumentClasses.get(i);
				String className = current.getName().toLowerCase();
				String argClassName = args[i].getClass().getName().toLowerCase();
				if(!className.contains(argClassName) && !argClassName.contains(className))
					invalid = true;
			}
		}else
			invalid = true;
		return invalid;
	}
}
