package com.jenjinstudios.client.net;

import com.jenjinstudios.client.message.ClientMessageFactory;
import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;

import java.security.*;
import java.util.LinkedList;
import java.util.List;
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
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	/** The number of milliseconds before a blocking method should time out. */
	private static final long TIMEOUT_MILLIS = 30000;
	/** The list of tasks that this client will execute each update cycle. */
	private final List<Runnable> repeatedTasks;
	/** The message factory used by this client. */
	private final ClientMessageFactory messageFactory;
	/** The period of the update in milliseconds. */
	private int period;
	/** The timer that manages the update loop. */
	private Timer sendMessagesTimer;
	/** The public key sent to the server. */
	private PublicKey clientPublicKey;
	/** The private key sent to the server. */
	private PrivateKey clientPrivateKey;
	private volatile boolean initialized;

	/**
	 * Construct a new client and attempt to connect to the server over the specified port.
	 */
	protected Client(MessageIO messageIO) {
		super(messageIO);
		repeatedTasks = new LinkedList<>();
		generateKeys();
		this.messageFactory = new ClientMessageFactory(getMessageRegistry());
	}

	public PublicKey getClientPublicKey() {
		return clientPublicKey;
	}

	/**
	 * Generate the public and private key used by this client.
	 */
	private void generateKeys() {
		try
		{
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			clientPrivateKey = keyPair.getPrivate();
			clientPublicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create RSA key pair!", e);
		}
	}

	/**
	 * Add a task to the repeated queue of this client.  Should be called to extend client functionality.
	 * @param r The task to be performed.
	 */
	public void addRepeatedTask(Runnable r) {
		synchronized (repeatedTasks)
		{
			repeatedTasks.add(r);
		}
	}

	/**
	 * Start the client, blocking until the client has successfully initialized.
	 * @return The success of the client start. initializing.
	 */
	public boolean blockingStart() {
		long startTime = System.currentTimeMillis();
		long timePast = System.currentTimeMillis() - startTime;
		start();

		while (!isAesKeySet() && (timePast < TIMEOUT_MILLIS))
		{
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.FINE, "Unable to sleep during blocking start.", e);
			}
			timePast = System.currentTimeMillis() - startTime;
		}

		return isAesKeySet();
	}

	/** Tell the client threads to stop running. */
	@Override
	public void shutdown() {
		super.shutdown();
		if (sendMessagesTimer != null)
		{
			sendMessagesTimer.cancel();
		}
	}

	/**
	 * Get the private key.
	 * @return The private key.
	 */
	public PrivateKey getClientPrivateKey() { return clientPrivateKey; }

	/**
	 * Get the update period of this client.
	 * @return The update period of this client.
	 */
	public int getPeriod() { return period; }

	/**
	 * Take care of all the necessary initialization messages between client and server.  These include things like RSA
	 * key exchanges and latency checks.
	 */
	public void doPostConnectInit(Message firstConnectResponse) {
		if (initialized)
		{
			throw new IllegalStateException("Trying to perform connection init when already initialized.");
		}
		int ups = (int) firstConnectResponse.getArgument("ups");
		period = 1000 / ups;

		// Next, queue up the PublicKeyMessage used to exchange the encrypted AES key used for encryption.
		Message publicKeyMessage = getMessageFactory().generatePublicKeyMessage(clientPublicKey);
		queueOutgoingMessage(publicKeyMessage);

		// Finally, send a ping request to establish latency.
		queueOutgoingMessage(messageFactory.generatePingRequest());

		initialized = true;

		sendMessagesTimer = new Timer("Client Update Loop", false);
		sendMessagesTimer.scheduleAtFixedRate(new ClientLoop(this), 0, period);
	}

	/** Run the repeated synchronized tasks. */
	void runRepeatedTasks() {
		synchronized (repeatedTasks)
		{
			for (Runnable r : repeatedTasks)
				r.run();
		}
	}

	@Override
	public ClientMessageFactory getMessageFactory() { return messageFactory; }
}
