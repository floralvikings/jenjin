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
	/** The list of tasks that this client will execute each update cycle. */
	private final List<Runnable> repeatedTasks;
	/** The message factory used by this client. */
	private final ClientMessageFactory messageFactory;
	/** The timer that manages the update loop. */
	private Timer sendMessagesTimer;
	/** The public key sent to the server. */
	private PublicKey publicKey;
	/** The private key sent to the server. */
	private PrivateKey privateKey;
	private volatile boolean initialized;
	private int ups;

	/**
	 * Construct a new client and attempt to connect to the server over the specified port.
	 */
	protected Client(MessageIO messageIO) {
		super(messageIO);
		repeatedTasks = new LinkedList<>();
		generateKeys();
		this.messageFactory = new ClientMessageFactory(getMessageRegistry());
	}

	public PublicKey getPublicKey() { return publicKey; }

	public boolean isInitialized() {
		return initialized;
	}

	public int getUps() {
		return ups;
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
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
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
	public PrivateKey getPrivateKey() { return privateKey; }

	/**
	 * Take care of all the necessary initialization messages between client and server.  These include things like RSA
	 * key exchanges and latency checks.
	 */
	public void doPostConnectInit(Message firstConnectResponse) {
		if (initialized)
		{
			throw new IllegalStateException("Trying to perform connection init when already initialized.");
		}
		ups = (int) firstConnectResponse.getArgument("ups");
		/* The period of the update in milliseconds. */
		int period = 1000 / ups;

		// Next, queue up the PublicKeyMessage used to exchange the encrypted AES key used for encryption.
		Message publicKeyMessage = messageFactory.generatePublicKeyMessage(publicKey);
		queueOutgoingMessage(publicKeyMessage);

		// Finally, send a ping request to establish latency.
		queueOutgoingMessage(messageFactory.generatePingRequest());

		initialized = true;

		sendMessagesTimer = new Timer("Client Update Loop", false);
		sendMessagesTimer.scheduleAtFixedRate(new ClientLoop(this), 0, period);
	}

	/** Run the repeated synchronized tasks. */
	protected void runRepeatedTasks() {
		synchronized (repeatedTasks)
		{
			for (Runnable r : repeatedTasks)
				r.run();
		}
	}
}
