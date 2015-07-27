package com.jenjinstudios.core.connection;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.concurrency.MessageThreadPool;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * The Connection class utilizes a MessageInputStream and MessageOutputStream to read and write messages to another
 * connection.  This is done with four seperate Threads, started using the start() method; one for reading messages, one
 * for executing the retrieved messages' ExecutableMessage equivalent, one for writing messages, and one for monitoring
 * the others for errors.
 *
 * @author Caleb Brinkman
 */
public class Connection<T extends MessageContext>
{
    private final MessageThreadPool<T> messageThreadPool;

	/**
	 * Construct a new Connection with the given configuration, input stream, and output stream.
     * @param contextClass The class used to instantiate the message context.
     * @param in The input stream.
     * @param out The output stream.
	 * @throws ConnectionInstantiationException If there is an exception when instantiating the connection.
	 */
    public Connection(Class<T> contextClass, MessageInputStream in, MessageOutputStream out)
          throws ConnectionInstantiationException
    {
		T context;
		try {
			context = contextClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException
			  e) {
			throw new ConnectionInstantiationException(e);
		}

		messageThreadPool = new MessageThreadPool(in, out, context);
	}

	/**
	 * Construct a new Connection with the given configuration.
	 * @param config The configuration of this connection.
	 * @throws ConnectionInstantiationException If there is an exception when instantiating the connection.
	 */
	public Connection(ConnectionConfig<T> config) throws ConnectionInstantiationException {
		InetAddress address = config.getAddress();
		Class<T> contextClass = config.getContextClass();

		T context;
		try {
			context = contextClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException
			  e) {
			throw new ConnectionInstantiationException(e);
		}

		for (String s : config.getMessageRegistryFiles()) {
			registerMessages(s);
		}

		MessageInputStream inputStream;
		MessageOutputStream outputStream;
		try {
            Socket socket = config.isSecure()
                  ? SSLSocketFactory.getDefault().createSocket(config.getAddress(), config.getPort())
                  : new Socket(config.getAddress(), config.getPort());
            inputStream = new MessageInputStream(socket.getInputStream());
            outputStream = new MessageOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			throw new ConnectionInstantiationException(e);
		}
		messageThreadPool = new MessageThreadPool(inputStream, outputStream, context);
		messageThreadPool.getMessageContext().setAddress(address);
	}

	/**
	 * Return whether the threads managed by this pool are running.
	 *
	 * @return Whether the threads managed by this pool are running.
	 */
	public boolean isRunning() { return messageThreadPool.isRunning(); }

	private void registerMessages(String s) throws ConnectionInstantiationException {
		File file = new File(s);
		InputStream stream;
		if (file.exists()) {
			try {
				stream = new FileInputStream(s);
			} catch (FileNotFoundException e) {
				throw new ConnectionInstantiationException(e);
			}
		} else {
			stream = getClass().getClassLoader().getResourceAsStream(s);
			if (stream == null) {
				throw new ConnectionInstantiationException("Unable to find message registry " + s);
			}
		}
		MessageRegistry.getGlobalRegistry().register(s, stream);
	}

    /**
     * Shutdown this connection.
     */
	public void shutdown() { messageThreadPool.shutdown(); }

	/**
	 * Start this connection.
	 */
	public void start() { messageThreadPool.start(); }

	/**
	 * Get the message context of this Connection.
	 *
	 * @return The message context of this Connection.
	 */
	public T getMessageContext() { return messageThreadPool.getMessageContext(); }

	/**
	 * Queue up the supplied message to be written.
	 *
	 * @param message The message to be sent.
	 */
	public void enqueueMessage(Message message) { messageThreadPool.enqueueMessage(message); }

	/**
	 * Get the unique ID of this MessageThreadPool.
	 *
	 * @return The unique ID of this MessageThreadPool.
	 */
	public String getId() { return messageThreadPool.getId(); }

    @Override
    public String toString() {
        return "Connection: " + getId() + " (" + getMessageContext().getAddress() + ')';
    }
}
