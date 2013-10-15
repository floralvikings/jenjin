package com.jenjinstudios.jgcf;

import com.jenjinstudios.io.MessageInputStream;
import com.jenjinstudios.io.MessageOutputStream;
import com.jenjinstudios.jgcf.message.ClientExecutableMessage;
import com.jenjinstudios.message.ExecutableMessage;
import com.jenjinstudios.message.Message;

import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for any client.  This class uses a similar system to the JGSA.
 *
 * @author Caleb Brinkman
 */
public class Client extends Thread
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	/** The port over which the client communicates with the server. */
	private final int PORT;
	/** The address of the server to which this client will connect. */
	private final String ADDRESS;
	/** The collection of messages to send at the next broadcast. */
	private final LinkedList<Message> outgoingMessages;
	/** The list of tasks that this client will execute each update cycle. */
	private final LinkedList<Runnable> repeatedSyncedTasks;
	/** The "one-shot" tasks to be executed in the current client loop. */
	private final LinkedList<Runnable> syncedTasks;
	/** The period of the update in milliseconds. */
	private int period;
	/** The socket used to connect to the server. */
	private Socket socket;
	/** Flags whether this client is connected. */
	private volatile boolean connected;
	/** The timer that manages the update loop. */
	private Timer sendMessagesTimer;
	/** Flags whether the client threads should be running. */
	private volatile boolean running;
	/** The input stream used to read messages from the server. */
	private MessageInputStream inputStream;
	/** The output stream used to write messages to the server. */
	private MessageOutputStream outputStream;
	/** The public key sent to the server. */
	private PublicKey publicKey;
	/** The private key sent to the server. */
	private PrivateKey privateKey;
	/** The AES key of this client. */
	private byte[] aesKey;
	/** The list of collected ping times. */
	private final ArrayList<Long> pingTimes;

	/**
	 * Construct a new client and attempt to connect to the server over the specified port.
	 *
	 * @param address The address of the server to which to connect
	 * @param port The port over which to connect to the server.
	 */
	protected Client(String address, int port)
	{
		ADDRESS = address;
		PORT = port;
		connected = false;
		outgoingMessages = new LinkedList<>();
		repeatedSyncedTasks = new LinkedList<>();
		syncedTasks = new LinkedList<>();
		pingTimes = new ArrayList<>();

		try
		{
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create RSA key pair!", e);
		}
	}

	/**
	 * Attempt to connect to the server at {@code ADDRESS} over {@code PORT}  This method must be called <i>before</i> the
	 * client thread is started.
	 */
	private void connect()
	{
		// TODO All the Key swapping and ping timing should be handled here.
		if (isConnected())
			return;
		try
		{
			socket = new Socket(ADDRESS, PORT);
			outputStream = new MessageOutputStream(socket.getOutputStream());
			inputStream = new MessageInputStream(socket.getInputStream());
			doPostConnectInit();
			connected = true;
		} catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Unable to connect to server.", ex);
		}
	}

	/**
	 * Take care of all the necessary initialization messages between client and server.  These include things like RSA key
	 * exchanges and latency checks.
	 *
	 * @throws IOException If there's an IOException when attempting to communicate with the server.
	 */
	private void doPostConnectInit() throws IOException
	{
		// First, get and process the required FirstConnectResponse message from the server.
		Message firstConnectResponse = inputStream.readMessage();
		int ups = (int) firstConnectResponse.getArgument("ups");
		period = 1000 / ups;

		// Next, queue up the PublicKeyMessage used to exchange the encrypted AES key used for encryption.
		Message publicKeyMessage = new Message("PublicKeyMessage");
		publicKeyMessage.setArgument("key", publicKey.getEncoded());
		sendMessage(publicKeyMessage);

		// Finally, send a ping request to establish latency.
		sendPing();
	}

	/**
	 * Add a task to the repeated queue of this client.  Should be called to extend client functionality.
	 *
	 * @param r The task to be performed.
	 */
	protected void addRepeatedTask(Runnable r)
	{
		synchronized (repeatedSyncedTasks)
		{
			repeatedSyncedTasks.add(r);
		}
	}

	/**
	 * Send all messages in the outgoing queue.  This method should only be called from the client update thread.
	 *
	 * @throws IOException If there is an error writing to the output stream.
	 */
	protected void sendAllMessages() throws IOException
	{
		synchronized (outgoingMessages)
		{
			while (!outgoingMessages.isEmpty()) outputStream.writeMessage(outgoingMessages.pop());
		}
	}

	/**
	 * Queue a message in the outgoing messages. This method is thread safe.
	 *
	 * @param message The message to add to the outgoing queue.
	 */
	public void sendMessage(Message message)
	{
		synchronized (outgoingMessages)
		{
			outgoingMessages.add(message);
		}
	}

	/**
	 * Process the specified message.  This method should be overridden by any implementing classes, but it does contain
	 * functionality necessary to communicate with a DownloadServer or a ChatServer.
	 *
	 * @param message The message to be processed.
	 */
	protected void processMessage(Message message)
	{
		ExecutableMessage exec;
		exec = ClientExecutableMessage.getClientExecutableMessageFor(this, message);
		if (exec != null)
		{
			exec.runASync();
			synchronized (syncedTasks)
			{
				syncedTasks.add(exec);
			}
		} else
		{
			Message invalid = new Message("InvalidMessage");
			invalid.setArgument("messageName", message.name);
			invalid.setArgument("messageID", message.getID());
			sendMessage(invalid);
		}
	}

	/** Tell the client threads to stop running. */
	public void shutdown()
	{
		running = false;
		closeLink();
	}

	/** Close the link with the server. */
	private void closeLink()
	{
		sendMessagesTimer.cancel();

		try
		{
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ex)
		{
			// Do nothing since we're closing the link anyway?
		} finally
		{
			connected = false;
		}
	}

	/**
	 * Start the client, blocking until the client has successfully initialized.
	 *
	 * @throws InterruptedException If an InterruptedException is thrown while waiting for the client to finish
	 * initializing.
	 */
	public void blockingStart() throws InterruptedException
	{
		start();

		while (!running) Thread.sleep(1);
		while (aesKey == null) Thread.sleep(1);
	}

	@Override
	public final void run()
	{
		if (!isConnected()) connect();
		// The ClientLoop is used to send messages in the outgoing queue and do syncrhonized executables.
		running = true;
		sendMessagesTimer = new Timer("Client Update Loop", false);
		sendMessagesTimer.scheduleAtFixedRate(new ClientLoop(this), 0, period);
		// This loop processes incoming messages.
		try
		{
			Message currentMessage;
			while ((currentMessage = inputStream.readMessage()) != null && running)
				processMessage(currentMessage);
		} catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Error retrieving message from server.", ex);
		} finally
		{
			shutdown();
		}
	}

	/**
	 * Flags whether the client threads should be running.
	 *
	 * @return true if this client thread is still running.
	 */
	public boolean isRunning()
	{ return running; }

	/**
	 * Flags whether this client is connected.
	 *
	 * @return true if this client is currently connected to a server.
	 */
	public boolean isConnected()
	{ return connected; }

	/**
	 * Get the list of repeating tasks.
	 *
	 * @return The list of repeating tasks.
	 */
	public LinkedList<Runnable> getRepeatedSyncedTasks()
	{
		return repeatedSyncedTasks;
	}

	/**
	 * The "one-shot" tasks to be executed in the current client loop.
	 *
	 * @return The list of Synced Tasks
	 */
	protected LinkedList<Runnable> getSyncedTasks()
	{
		LinkedList<Runnable> temp = new LinkedList<>();
		synchronized (syncedTasks)
		{
			temp.addAll(syncedTasks);
			syncedTasks.removeAll(temp);
		}
		return temp;

	}

	/** Send a ping request. */
	public void sendPing()
	{
		Message pingRequest = new Message("PingRequest");
		pingRequest.setArgument("requestTimeNanos", System.nanoTime());
		sendMessage(pingRequest);
	}

	/**
	 * Add a ping time to the list.
	 *
	 * @param pingTime The time of the ping, in nanoseconds.
	 */
	public void addPingTime(long pingTime)
	{
		pingTimes.add(pingTime);
	}

	/**
	 * Get the average ping time, in nanoseconds.
	 *
	 * @return The average ping time between client and server, in nanoseconds.
	 */
	public long getAveragePingTime()
	{
		long total = 0;
		int num;
		synchronized (pingTimes)
		{
			num = pingTimes.size();
			for (long l : pingTimes) total += l;
		}
		return total / num;
	}

	/**
	 * Get the private key.
	 *
	 * @return The private key.
	 */
	public PrivateKey getPrivateKey()
	{
		return privateKey;
	}

	/**
	 * Set the AES key used by this client.
	 *
	 * @param key The key used by this client.
	 */
	public void setAESKey(byte[] key)
	{
		aesKey = key;
		inputStream.setAESKey(key);
		outputStream.setAesKey(key);
	}

	/**
	 * Get the update period of this client.
	 *
	 * @return The update period of this client.
	 */
	public int getPeriod()
	{
		return period;
	}
}
