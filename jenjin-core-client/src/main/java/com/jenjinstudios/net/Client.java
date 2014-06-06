package com.jenjinstudios.net;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.message.ClientExecutableMessage;
import com.jenjinstudios.util.ClientMessageFactory;

import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.util.LinkedList;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for any client.  This class uses a similar system to the JGSA.
 * @author Caleb Brinkman
 */
public class Client extends Connection
{
	/** The logger associated with this class. */
	public static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	/** The number of milliseconds before a blocking method should time out. */
	private static final long TIMEOUT_MILLIS = 30000;
	/** The port over which the client communicates with the server. */
	private final int PORT;
	/** The address of the server to which this client will connect. */
	private final String ADDRESS;
	/** The list of tasks that this client will execute each update cycle. */
	private final LinkedList<Runnable> repeatedSyncedTasks;
	private final ClientMessageFactory messageFactory;
	/** The period of the update in milliseconds. */
	private int period;
	/** The timer that manages the update loop. */
	private Timer sendMessagesTimer;
	/** The public key sent to the server. */
	private PublicKey publicKey;
	/** The private key sent to the server. */
	private PrivateKey privateKey;

	/**
	 * Construct a new client and attempt to connect to the server over the specified port.
	 * @param address The address of the server to which to connect
	 * @param port The port over which to connect to the server.
	 */
	protected Client(String address, int port) {
		ADDRESS = address;
		PORT = port;
		repeatedSyncedTasks = new LinkedList<>();
		setMessageRegistry(new MessageRegistry(false));
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
		this.messageFactory = new ClientMessageFactory(this);
	}

	/**
	 * Add a task to the repeated queue of this client.  Should be called to extend client functionality.
	 * @param r The task to be performed.
	 */
	public void addRepeatedTask(Runnable r) {
		synchronized (repeatedSyncedTasks)
		{
			repeatedSyncedTasks.add(r);
		}
	}

	/**
	 * Start the client, blocking until the client has successfully initialized.
	 * @return The success of the client start.
	 * @throws InterruptedException If an InterruptedException is thrown while waiting for the client to finish
	 * initializing.
	 */
	public boolean blockingStart() throws InterruptedException {
		long startTime = System.currentTimeMillis();
		long timePast = System.currentTimeMillis() - startTime;
		start();

		while ((!isRunning() || (getAesKey() == null)) && (timePast < TIMEOUT_MILLIS))
		{
			Thread.sleep(10);
			timePast = System.currentTimeMillis() - startTime;
		}

		return isRunning() && (getAesKey() != null);
	}

	@Override
	public final void run() {
		if (!isConnected()) connect();
		if (!isConnected())
			return;
		// The ClientLoop is used to send messages in the outgoing queue and do synchronized actions.
		sendMessagesTimer = new Timer("Client Update Loop", false);
		sendMessagesTimer.scheduleAtFixedRate(new ClientLoop(this), 0, period);

		super.run();
	}

	/** Tell the client threads to stop running. */
	public void shutdown() {
		super.shutdown();
		sendMessagesTimer.cancel();
		closeLink();
	}

	/**
	 * Add a task to the list of repeated synchronized tasks.
	 * @param r The task to add.
	 */
	public void addRepeatedSyncedTask(Runnable r) {
		synchronized (repeatedSyncedTasks)
		{
			repeatedSyncedTasks.add(r);
		}
	}

	/**
	 * Get the private key.
	 * @return The private key.
	 */
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * Get the update period of this client.
	 * @return The update period of this client.
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * Attempt to connect to the server at {@code ADDRESS} over {@code PORT}  This method must be called <i>before</i> the
	 * client thread is started.
	 */
	private void connect() {
		if (isConnected()) // No need to connect if we're already connected.
			return;
		try
		{
			super.setSocket(new Socket(ADDRESS, PORT));
			super.setConnected(doPostConnectInit());
		} catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Unable to connect to server.", ex);
		}
	}

	/**
	 * Take care of all the necessary initialization messages between client and server.  These include things like RSA key
	 * exchanges and latency checks.
	 * @return Whether the init was successful.
	 * @throws IOException If there's an IOException when attempting to communicate with the server.
	 */
	private boolean doPostConnectInit() {
		// First, get and process the required FirstConnectResponse message from the server.
		Message firstConnectResponse = getInputStream().readMessage();
		if (firstConnectResponse == null)
		{
			return false;
		}
		int ups = (int) firstConnectResponse.getArgument("ups");
		period = 1000 / ups;

		// Next, queue up the PublicKeyMessage used to exchange the encrypted AES key used for encryption.
		Message publicKeyMessage = getMessageFactory().generatePublicKeyMessage(publicKey);
		queueMessage(publicKeyMessage);

		// Finally, send a ping request to establish latency.
		sendPing();
		return true;
	}

	/**
	 * Get an executable message for a given message.
	 * @param message The message to be used.
	 * @return The ExecutableMessage.
	 */
	@Override
	protected ClientExecutableMessage getExecutableMessage(Message message) {
		return (ClientExecutableMessage) ClientExecutableMessage.getClientExecutableMessageFor(this, message);
	}

	/** Run the repeated synchronized tasks. */
	void runRepeatedSyncedTasks() {
		synchronized (repeatedSyncedTasks)
		{
			for (Runnable r : repeatedSyncedTasks)
				r.run();
		}
	}

	public ClientMessageFactory getMessageFactory() { return messageFactory; }
}
