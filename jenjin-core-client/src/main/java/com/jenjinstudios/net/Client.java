package com.jenjinstudios.net;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.message.ClientMessageFactory;

import java.io.IOException;
import java.net.Socket;
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
	public static final Logger LOGGER = Logger.getLogger(Client.class.getName());
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
	private PublicKey publicKey;
	/** The private key sent to the server. */
	private PrivateKey privateKey;
	private Socket socket;

	/**
	 * Construct a new client and attempt to connect to the server over the specified port.
	 * @param socket The Socket over which this client will communicate with the server.
	 */
	protected Client(Socket socket) {
		super(new MessageRegistry());
		this.socket = socket;
		repeatedTasks = new LinkedList<>();
		generateKeys();
		this.messageFactory = new ClientMessageFactory(getMessageRegistry());
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
		connect();
		super.run();
	}

	/** Tell the client threads to stop running. */
	public void shutdown() {
		super.shutdown();
		sendMessagesTimer.cancel();
		closeLink();
	}

	/**
	 * Get the private key.
	 * @return The private key.
	 */
	public PrivateKey getPrivateKey() { return privateKey; }

	/**
	 * Get the update period of this client.
	 * @return The update period of this client.
	 */
	public int getPeriod() { return period; }

	/**
	 * Attempt to connect to the server at {@code ADDRESS} over {@code PORT}  This method must be called <i>before</i>
	 * the client thread is started.
	 */
	private void connect() {
		try
		{
			super.setSocket(socket);
		} catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Unable to connect to server.", ex);
		}
	}

	/**
	 * Take care of all the necessary initialization messages between client and server.  These include things like RSA
	 * key exchanges and latency checks.
	 */
	public void doPostConnectInit(Message firstConnectResponse) {
		if (isConnected())
		{
			throw new IllegalStateException("Trying to perform connection init when already connected.");
		}
		int ups = (int) firstConnectResponse.getArgument("ups");
		period = 1000 / ups;

		// Next, queue up the PublicKeyMessage used to exchange the encrypted AES key used for encryption.
		Message publicKeyMessage = getMessageFactory().generatePublicKeyMessage(publicKey);
		queueMessage(publicKeyMessage);

		// Finally, send a ping request to establish latency.
		sendPing();

		super.setConnected(true);

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

	public ClientMessageFactory getMessageFactory() { return messageFactory; }
}
