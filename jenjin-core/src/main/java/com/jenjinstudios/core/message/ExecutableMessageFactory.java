package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.xml.MessageType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to generate ExecutableMessages.
 *
 * @author Caleb Brinkman
 */
public class ExecutableMessageFactory
{
    private static final Logger LOGGER = Logger.getLogger(ExecutableMessageFactory.class.getName());
    public static final Constructor[] EMPTY_CONSTRUCTOR_ARRAY = new Constructor[0];
    private final Connection connection;

    /**
     * Construct an ExecutableMessageFactory for the specified connection.
     *
     * @param connection The connection for which this factory will produce ExecutableMessages.
     */
    public ExecutableMessageFactory(Connection connection) { this.connection = connection; }

    /**
     * Given a {@code Connection} and a {@code Message}, create and return an appropriate {@code ExecutableMessage}.
     *
     * @param message The {@code Message} for which the {@code ExecutableMessage} is being created.
     *
     * @return The {@code ExecutableMessage} created for {@code connection} and {@code message}.
     */
    public List<ExecutableMessage> getExecutableMessagesFor(Message message) {
        List<ExecutableMessage> executableMessages = new LinkedList<>();
        Collection<Constructor> execConstructors = getExecConstructors(message);

        for (Constructor c : execConstructors)
        {
            if (c != null)
            {
                executableMessages.add(createExec(message, c));
            } else
            {
                Object[] args = {connection.getClass().getName(), message.name};
                String report = "No constructor containing Connection or {0} as first argument type found for {1}";
                LOGGER.log(Level.SEVERE, report, args);
            }
        }
        return executableMessages;
    }

    private Collection<Constructor> getExecConstructors(Message message) {
        Collection<Constructor> constructors = new LinkedList<>();
        MessageType messageType = MessageRegistry.getInstance().getMessageType(message.getID());
        for (String className : messageType.getExecutables())
        {
            Constructor[] execConstructors = EMPTY_CONSTRUCTOR_ARRAY;
            try
            {
                Class execClass = Class.forName(className);
                execConstructors = execClass.getConstructors();
            } catch (ClassNotFoundException | NullPointerException e)
            {
                LOGGER.log(Level.WARNING, "Could not find class: {0}", className);
            }
            constructors.add(getAppropriateConstructor(execConstructors));
        }
        return constructors;
    }

    private ExecutableMessage createExec(Message msg, Constructor constructor) {
        ExecutableMessage executableMessage = null;
        try
        {
            executableMessage = (ExecutableMessage) constructor.newInstance(connection, msg);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e)
        {
            LOGGER.log(Level.SEVERE, "Constructor not correct", e);
        }
        return executableMessage;
    }

    private Constructor getAppropriateConstructor(Constructor... execConstructors) {
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
