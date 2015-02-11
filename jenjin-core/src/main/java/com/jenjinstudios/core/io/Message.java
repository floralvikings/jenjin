package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.MessageType;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code Message} class is used in sending data to and receiving data from {@code Connection} objects.  Each
 * Message has a unique {@code name}, a unique {@code id}, and a {@code Map} of arguments which are accessed with the
 * {@code getArgument} and {@code setObject} methods. </p> Message arguments may consist of any primitive type, as well
 * as {@code String} objects, and {@code String} and {@code byte} arrays.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("ClassWithTooManyDependents")
public class Message
{
    private static final Logger LOGGER = Logger.getLogger(Message.class.getName());
    /** The unique name of this type of Message. */
    public final String name;
    private final MessageType messageType;
    private final Map<String, Object> argumentsByName;
    private final short id;

    Message(MessageType type, Object... args) {
        this.id = type.getId();
        messageType = type;
        name = messageType.getName();
        argumentsByName = new TreeMap<>();
        if (args.length != messageType.getArguments().size())
        {
            throw new IllegalArgumentException("Incorrect number of arguments provided for Message");
        }
        for (int i = 0; i < messageType.getArguments().size(); i++)
        {
            setArgument(messageType.getArguments().get(i).getName(), args[i]);
        }
    }

    Message(MessageType messageType) {
        this.messageType = messageType;
        this.name = messageType.getName();
        id = messageType.getId();
        argumentsByName = new TreeMap<>();
    }

    /**
     * Get the Class that represents the primitive type of the given name.
     *
     * @param name The name of the type.  One of: <ul> <li>byte</li> <li>short</li> <li>char</li> <li>int</li>
     * <li>float</li> <li>long</li> <li>double</li> <li>String</li> <li>String[]</li> <li>byte[]</li> </ul>
     *
     * @return The Class represented by {@code name}.
     */
    public static Class getTypeForName(String name) {
        Class type = getPrimitiveClass(name);
        if (type == null)
        {
            try
            {
                type = Class.forName(name);
            } catch (ClassNotFoundException ex)
            {
                LOGGER.log(Level.WARNING, "Unknown Primitive Type", ex);
                type = Object.class;
            }
        }
        return type;
    }

    private static Class getPrimitiveClass(String name) {
        Class type;
        switch (name)
        {
            case "boolean":
                type = Boolean.class;
                break;
            case "byte":
                type = Byte.class;
                break;
            case "short":
                type = Short.class;
                break;
            case "int":
                type = Integer.class;
                break;
            case "long":
                type = Long.class;
                break;
            case "float":
                type = Float.class;
                break;
            case "double":
                type = Double.class;
                break;
            case "String":
                type = String.class;
                break;
            default:
                type = getArrayType(name);
                break;
        }
        return type;
    }

    private static Class getArrayType(String name) {
        Class type;
        switch (name)
        {
            case "byte[]":
                type = byte[].class;
                break;
            case "String[]":
                type = String[].class;
                break;
            default:
                type = null;
                break;
        }
        return type;
    }

    /**
     * Set the argument with the given name to the argument of the given value.
     *
     * @param argumentName The name of the argument.
     * @param argument The value to be stored in the argument.
     *
     * @throws java.lang.IllegalArgumentException If the name or type of of the argument is invalid.
     */
    public void setArgument(String argumentName, Object argument) {
        ArgumentType argType = null;
        for (ArgumentType a : messageType.getArguments())
        {
            if (argumentName.equals(a.getName()))
            {
                argType = a;
            }
        }
        if (argType == null)
        {
            throw new IllegalArgumentException("Invalid argument name for Message: " + argumentName +
                  " (Message type: " + messageType.getName() + ')');
        }
        @SuppressWarnings("rawtypes")
        Class argumentClass = getTypeForName(argType.getType());
        if (!argumentClass.isInstance(argument))
        {
            throw new IllegalArgumentException("Invalid argument type for Message: " + argument +
                  " (Expected " + argType.getType() + ", got " + argument.getClass() + ')');
        }
        argumentsByName.put(argumentName, argument);
    }

    /**
     * Get the argument with the given name.
     *
     * @param argumentName The name of the argument.
     *
     * @return The value of the argument specified by {@code argumentName}, or null if the specified argument does not
     * exist.
     */
    public Object getArgument(String argumentName) { return argumentsByName.get(argumentName); }

    /**
     * Get the id of this type of Message.
     *
     * @return The id of this type of Message.
     */
    public short getID() { return id; }

    /**
     * Get a copy of the array containing the arguments passed to this Message.
     *
     * @return A copy of the array containing the arguments passed to this Message.
     */
    public final Object[] getArgs() {
        if (isInvalid())
        {
            throw new IllegalStateException("Attempting to retrieve arguments while message is invalid. (Not all " +
                  "arguments have been set.)");
        }
        Object[] argsArray = new Object[messageType.getArguments().size()];
        for (int i = 0; i < messageType.getArguments().size(); i++)
        {
            argsArray[i] = argumentsByName.get(messageType.getArguments().get(i).getName());
        }
        return argsArray;
    }

    boolean isInvalid() { return argumentsByName.size() != messageType.getArguments().size(); }

    @Override
    public String toString() { return "Message " + id + ' ' + name; }
}
