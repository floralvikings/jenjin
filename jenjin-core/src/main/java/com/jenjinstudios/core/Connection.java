package com.jenjinstudios.core;

import com.jenjinstudios.core.concurrency.MessageReader;
import com.jenjinstudios.core.concurrency.MessageWriter;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.xml.MessageType;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Connection class utilizes a MessageInputStream and MessageOutputStream to read and write messages to another
 * connection.  This is done with four seperate Threads, started using the start() method; one for reading messages, one
 * for executing the retrieved messages' ExecutableMessage equivalent, one for writing messages, and one for monitoring
 * the others for errors.
 *
 * @author Caleb Brinkman
 */
public class Connection
{
	private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());
	private final PingTracker pingTracker;
	private final MessageIO messageIO;
	private final Timer messageExecutionTimer;
	private final Timer checkErrorTimer;
	private final TimerTask messageExecutionTask;
	private final TimerTask checkErrorTask;
	private final MessageWriter messageWriter;
	private final MessageReader messageReader;
	private String name = "Connection";

	/**
	 * Construct a new connection using the given MessageIO for reading and writing messages.
	 *
	 * @param streams The MessageIO containing the input and output streams
	 */
	public Connection(MessageIO streams) {
		this.messageIO = streams;
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/core/io/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core XML Registry", stream);
		messageWriter = new MessageWriter(messageIO.getOut());
		messageExecutionTimer = new Timer();
		messageExecutionTask = new MessageExecutor();
		pingTracker = new PingTracker();
		checkErrorTimer = new Timer();
		checkErrorTask = new CheckErrorsTask();
		messageReader = new MessageReader(messageIO.getIn());
	}

	/**
	 * Queue up the supplied message to be written.
	 *
	 * @param message The message to be sent.
	 */
	public void enqueueMessage(Message message) { messageWriter.enqueue(message); }

	/**
	 * Start the message reader thread managed by this connection.
	 */
	public void start() {
		messageExecutionTimer.scheduleAtFixedRate(messageExecutionTask, 0, 10);
		checkErrorTimer.scheduleAtFixedRate(checkErrorTask, 0, 10);
		messageReader.start();
		messageWriter.start();
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
	 * End this connection's execution loop and close any streams.
	 */
	public void shutdown() {
		LOGGER.log(Level.INFO, "Shutting down connection: " + name);
		messageWriter.stop();
		messageReader.stop();
		checkErrorTimer.cancel();
		messageExecutionTimer.cancel();
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

	private static class ExecutableMessageFactory
	{
		private static final Constructor[] EMPTY_CONSTRUCTOR_ARRAY = new Constructor[0];
		private final Connection connection;

		/**
		 * Construct an ExecutableMessageFactory for the specified connection.
		 *
		 * @param connection The connection for which this factory will produce ExecutableMessages.
		 */
		private ExecutableMessageFactory(Connection connection) { this.connection = connection; }

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
					LOGGER.log(Level.SEVERE, report, args);
				}
			}
			return executableMessages;
		}

		private Collection<Constructor> getExecConstructors(Message message) {
			Collection<Constructor> constructors = new LinkedList<>();
			MessageType messageType = MessageRegistry.getGlobalRegistry().getMessageType(message.getID());
			for (String className : messageType.getExecutables())
			{
				Constructor[] execConstructors = EMPTY_CONSTRUCTOR_ARRAY;
				try
				{
					Class execClass = Class.forName(className);
					execConstructors = execClass.getConstructors();
				} catch (ClassNotFoundException ex)
				{
					LOGGER.log(Level.WARNING, "Could not find class: " + className, ex);
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

	private class CheckErrorsTask extends TimerTask
	{
		@Override
		public void run() {
			if (messageReader.isErrored() || messageWriter.isErrored())
			{
				LOGGER.log(Level.SEVERE, "Message reader or writer in error state; shutting down.");
				shutdown();
			}
		}
	}

	protected class MessageExecutor extends TimerTask
	{
		private final ExecutableMessageFactory exMessageFactory;

		protected MessageExecutor() {
			exMessageFactory = new ExecutableMessageFactory(Connection.this);
		}

		@Override
		public void run() {
			Iterable<Message> messages = messageReader.getReceivedMessages();
			messages.forEach(this::executeMessage);
		}

		private void executeMessage(Message message) {
			List<ExecutableMessage> executables = exMessageFactory.getExecutableMessagesFor(message);
			for (ExecutableMessage executable : executables)
			{
				if (executable == null)
				{
					LOGGER.log(Level.WARNING, "Invalid message received from MessageReader");
					Message invalid = generateInvalidMessage(message.getID(), message.name);
					enqueueMessage(invalid);
				} else
				{
					executable.execute();
				}
			}
		}

		private Message generateInvalidMessage(short id, String messageName) {
			Message invalid = MessageRegistry.getGlobalRegistry().createMessage("InvalidMessage");
			invalid.setArgument("messageName", messageName);
			invalid.setArgument("messageID", id);
			return invalid;
		}
	}
}
