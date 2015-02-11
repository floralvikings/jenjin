package com.jenjinstudios.core;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageTypeException;
import com.jenjinstudios.core.xml.MessageType;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code Connection} class is a subclass of the {@code Thread} class; it will loop indefinitely until the {@code
 * shutdown} method is called, reading {@code Message} objects from a stream, and invoking the correct implementation of
 * {@code ExecutableMessage} each time a new message is received.
 *
 * @author Caleb Brinkman
 */
public class Connection
{
    private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
    private static final int KEYSIZE = 512;
    private final PingTracker pingTracker;
    private final ExecutableMessageQueue executableMessageQueue;
    private final MessageIO messageIO;
    private final Thread messageReaderThread;
    private String name = "Connection";
    private final Map<InetAddress, Key> verifiedKeys = new HashMap<>(10);

    /**
     * Construct a new {@code Connection} that utilizes the specified {@code MessageIO} to read and write messages.
     *
     * @param streams The {@code MessageIO} containing streams used to read and write messages.
     */
    protected Connection(MessageIO streams) {
        this.messageIO = streams;
        pingTracker = new PingTracker();
        executableMessageQueue = new ExecutableMessageQueue();
        messageReaderThread = new Thread(new RunnableMessageReader(this));
    }

    /**
     * Generate a PublicKeyMessage for the given {@code PublicKey}.
     *
     * @param publicKey The {@code PublicKey} for which to generate a {@code Message}.
     *
     * @return The generated message.
     */
    public static Message generatePublicKeyMessage(Key publicKey) {
        Message publicKeyMessage = MessageRegistry.getInstance().createMessage("PublicKeyMessage");
        publicKeyMessage.setArgument("publicKey", publicKey.getEncoded());
        return publicKeyMessage;
    }

    /**
     * Generate an RSA-512 Public-Private Key Pair.
     *
     * @return The generated {@code KeyPair}, or null if the KeyPair could not be created.
     */
    public static KeyPair generateRSAKeyPair() {
        KeyPair keyPair = null;
        try
        {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEYSIZE);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e)
        {
            LOGGER.log(Level.SEVERE, "Unable to create RSA key pair!", e);
        }
        return keyPair;
    }

    /**
     * Start the message reader thread managed by this connection.
     */
    public void start() {
        messageReaderThread.start();
    }

    /**
     * Set the RSA public/private key pair used to encrypt outgoing and decrypt incoming messages, and queue a message
     * containing the public key.
     *
     * @param rsaKeyPair The keypair to use for encryption/decrytion.
     */
    public void setRSAKeyPair(KeyPair rsaKeyPair) {
        if (rsaKeyPair != null)
        {
            messageIO.getIn().setPrivateKey(rsaKeyPair.getPrivate());
            Message message = generatePublicKeyMessage(rsaKeyPair.getPublic());
            messageIO.queueOutgoingMessage(message);
        }
    }

    /**
     * Get the MessageIO containing the keys and streams used by this connection.
     *
     * @return The MessageIO containing the keys and streams used by this connection.
     */
    public MessageIO getMessageIO() { return messageIO; }

    /**
     * Get the PingTracker used by this connection to track latency.
     *
     * @return The PingTracker used by this connection to track latency.
     */
    public PingTracker getPingTracker() { return pingTracker; }

    /**
     * Get the {@code ExecutableMessageQueue} maintained by this connection.
     *
     * @return The {@code ExecutableMessageQueue} maintained by this connection.
     */
    public ExecutableMessageQueue getExecutableMessageQueue() { return executableMessageQueue; }

    /**
     * End this connection's execution loop and close any streams.
     */
    public void shutdown() {
        messageIO.closeInputStream();
        messageIO.closeOutputStream();
    }

    /**
     * Get the name of this {@code Connection}.
     *
     * @return The name of this {@code Connection}.
     */
    public String getName() { return name; }

    /**
     * Set the name of this {@code Connection}.
     *
     * @param name The name of this {@code Connection}.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Get the map of domains and verified keys for this client.
     *
     * @return The map of domains and verified keys for this client.
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Map<InetAddress, Key> getVerifiedKeys() { return verifiedKeys; }

    /**
     * This class is used to continuously read {@code Message} objects from a {@code MessageInputStream}, invoke the
     * appropriate {@code ExecutableMessage}, and store it so that the {@code runeDelayed} method may be called later.
     *
     * @author Caleb Brinkman
     */
    public static class RunnableMessageReader implements Runnable
    {
        private static final int MAX_INVALID_MESSAGES = 10;
        private static final Logger INNER_LOGGER = Logger.getLogger(RunnableMessageReader.class.getName());
        private final Connection connection;
        private int invalidMsgCount;

        /**
         * Construct a new {@code RunnableMessageReader} working for the given Connection.
         *
         * @param connection The {@code Connection} managing this reader.
         */
        public RunnableMessageReader(Connection connection) {
            this.connection = connection;
        }

        /**
         * Generate an InvalidMessage message for the given invalid ID and message name.
         *
         * @param id The ID of the invalid message.
         * @param name The Name of the invalid message.
         *
         * @return The generated InvalidMessage object.
         */
        private static Message generateInvalidMessage(short id, String name) {
            Message invalid = MessageRegistry.getInstance().createMessage("InvalidMessage");
            invalid.setArgument("messageName", name);
            invalid.setArgument("messageID", id);
            return invalid;
        }

        @Override
        public void run() {
            boolean success = true;
            while ((invalidMsgCount < MAX_INVALID_MESSAGES) && success)
            {
                try
                {
                    Message currentMessage = connection.getMessageIO().getIn().readMessage();
                    executeMessage(currentMessage);
                } catch (MessageTypeException e)
                {
                    reportInvalidMessage(e);
                } catch (EOFException | SocketException e)
                {
                    INNER_LOGGER.log(Level.FINER, "Connection closed: " + connection.getName(), e);
                    success = false;
                } catch (IOException e)
                {
                    INNER_LOGGER.log(Level.FINE, "IOException when attempting to read from stream.", e);
                    success = false;
                }
            }
        }

        void executeMessage(Message message) {
            ExecutableMessageFactory messageFactory = new ExecutableMessageFactory(connection);
            Collection<ExecutableMessage> execs = messageFactory.getExecutableMessagesFor(message);
            for (ExecutableMessage exec : execs)
            {
                if (exec != null)
                {
                    processExecutableMessage(exec);
                } else
                {
                    processInvalidMessage(message);
                }
            }
        }

        private void processInvalidMessage(Message message) {
            Message invalid = generateInvalidMessage(message.getID(), message.name);
            connection.getMessageIO().queueOutgoingMessage(invalid);
        }

        private void processExecutableMessage(ExecutableMessage exec) {
            exec.runImmediate();
            connection.getExecutableMessageQueue().queueExecutableMessage(exec);
        }

        void reportInvalidMessage(MessageTypeException e) {
            INNER_LOGGER.log(Level.WARNING, "Input stream reported invalid message receipt.");
            Message unknown = generateInvalidMessage(e.getId(), "Unknown");
            connection.getMessageIO().queueOutgoingMessage(unknown);
            invalidMsgCount++;
        }
    }

    /**
     * Used to generate ExecutableMessages.
     *
     * @author Caleb Brinkman
     */
    public static class ExecutableMessageFactory
    {
        private static final Logger INNER_LOGGER = Logger.getLogger(ExecutableMessageFactory.class.getName());
        private static final Constructor[] EMPTY_CONSTRUCTOR_ARRAY = new Constructor[0];
        private final Connection connection;

        /**
         * Construct an ExecutableMessageFactory for the specified connection.
         *
         * @param connection The connection for which this factory will produce ExecutableMessages.
         */
        public ExecutableMessageFactory(Connection connection) { this.connection = connection; }

        /**
         * Given a {@code Connection} and a {@code Message}, create and return an appropriate {@code
         * ExecutableMessage}.
         *
         * @param message The {@code Message} for which the {@code ExecutableMessage} is being created.
         *
         * @return The {@code ExecutableMessage} created for {@code connection} and {@code message}.
         */
        public List<ExecutableMessage> getExecutableMessagesFor(Message message) {
            List<ExecutableMessage> executableMessages = new LinkedList<>();
            Collection<Constructor> execConstructors = getExecConstructors(message);

            for (Constructor constructor : execConstructors)
            {
                if (constructor != null)
                {
                    executableMessages.add(createExec(message, constructor));
                } else
                {
                    Object[] args = {connection.getClass().getName(), message.name};
                    String report = "No constructor containing Connection or {0} as first argument type found for {1}";
                    INNER_LOGGER.log(Level.SEVERE, report, args);
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
                } catch (ClassNotFoundException ex)
                {
                    INNER_LOGGER.log(Level.WARNING, "Could not find class: " + className, ex);
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
                INNER_LOGGER.log(Level.SEVERE, "Constructor not correct", e);
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
}
