package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;

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
    private final Connection connection;

    /**
     * Construct a new ExecutableMessage; this should only ever be invoked reflectively, by a {@code Connection}'s
     * update cycle.
     *
     * @param connection The connection for which this ExecutbleMessage will work.
     * @param message The message that caused this {@code ExecutableMessage} to be created.
     */
    protected ExecutableMessage(Connection connection, Message message) {
        this.message = message;
        this.connection = connection;
    }

    /**
     * This method is invoked by the {@code Connection} execution timer, and should not be called directly.
     */
    public abstract void runDelayed();

    /**
     * This method is invoked by a {@code Connection} when a message is received and the {@code ExecutableMessage} is
     * created, and should not be called directly.
     */
    public abstract void runImmediate();

    /**
     * Get the message for which this {@code ExecutableMessage} was created.
     *
     * @return The message for which this {@code ExecutableMessage} was created.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Get the connection associated with this ExecutableMessage.
     *
     * @return The connection associated with this ExecutableMessage.
     */
    public Connection getConnection() { return connection; }
}
