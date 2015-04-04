package com.jenjinstudios.client.net;

import com.jenjinstudios.client.authentication.User;
import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageStreamPair;

import java.io.InputStream;
import java.security.KeyPair;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for any client.  This class uses a similar system to the JGSA.
 *
 * @author Caleb Brinkman
 */
public class Client<T extends ClientMessageContext> extends Connection<T>
{
	static final int THIRTY_SECONDS = 30000;
	private static final int UPDATES_PER_SECOND = 60;
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	private final User user;
	private final LoginTracker loginTracker;
	private final List<Runnable> repeatedTasks;
    private Timer sendMessagesTimer;
    private final ClientLoop clientLoop = new ClientLoop(this);

    /**
     * Construct a new client and attempt to connect to the server over the specified port.
     *
	 * @param messageStreamPair The MessageIO used to send and receive messages.
	 */
	protected Client(MessageStreamPair messageStreamPair, T context) {
		super(messageStreamPair, context);
		this.loginTracker = getMessageContext().getLoginTracker();
		getMessageContext().setUser(user);
		repeatedTasks = new LinkedList<>();
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/client/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core Client/Server Messages", stream);
		this.user = user;
	}

    /**
     * Generate a PingRequest message.
     *
     * @return The generated message.
     */
    private static Message generatePingRequest() {
		Message pingRequest = MessageRegistry.getGlobalRegistry().createMessage("PingRequest");
		pingRequest.setArgument("requestTimeMillis", System.currentTimeMillis());
        return pingRequest;
    }

	/**
	 * Generate a LogoutRequest message.
	 *
	 * @return The LogoutRequestMessage.
	 */
	public static Message generateLogoutRequest() {
		return MessageRegistry.getGlobalRegistry().createMessage
			  ("LogoutRequest");
	}

	/**
	 * Generate a LoginRequest message.  This message will be encrypted if possible.
	 *
	 * @param user The User for which to generate the login request.
	 *
	 * @return The LoginRequest message.
	 */
	public static Message generateLoginRequest(User user) {// Create the login request.
		Message loginRequest = MessageRegistry.getGlobalRegistry().createMessage("LoginRequest");
		loginRequest.setArgument("username", user.getUsername());
		loginRequest.setArgument("password", user.getPassword());
		return loginRequest;
	}

	static void waitTenMillis() {
		try
		{
			Thread.sleep(10);
		} catch (InterruptedException e)
		{
			LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
		}
	}

	/**
	 * Add a task to the repeated queue of this client.  Should be called to extend client functionality.
     *
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

    @Override
    public void start() {
        KeyPair rsaKeyPair = generateRSAKeyPair();
        setRSAKeyPair(rsaKeyPair);

        // Finally, send a ping request to establish latency.
		enqueueMessage(generatePingRequest());

        sendMessagesTimer = new Timer("Client Update Loop", false);
        int period = 1000 / UPDATES_PER_SECOND;
        sendMessagesTimer.scheduleAtFixedRate(clientLoop, 0, period);

		super.start();
	}

	/**
	 * Send a logout request and block execution until the response is received.
	 */
	public void logoutAndWait() {
		sendLogoutRequest();
		long startTime = System.currentTimeMillis();
		while (loginTracker.isWaitingForResponse() && ((System.currentTimeMillis() - startTime) < THIRTY_SECONDS))
		{
			waitTenMillis();
		}
	}

	/**
	 * Send a login request and await the response.
	 *
	 * @return Whether the login was successful.
	 */
	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	public boolean loginAndWait() {
		sendLoginRequest();
		long startTime = System.currentTimeMillis();
		while (loginTracker.isWaitingForResponse() && ((System.currentTimeMillis() - startTime) < THIRTY_SECONDS))
		{
			waitTenMillis();
		}
		return loginTracker.isLoggedIn();
	}

	/**
	 * Get the LoginTracker managed by this client.
	 *
	 * @return The login tracker managed by this client.
	 */
	public LoginTracker getLoginTracker() { return loginTracker; }

	/**
	 * Get the username of this client.
	 *
	 * @return The username of this client.
	 */
	public User getUser() { return user; }

	/** Run the repeated synchronized tasks. */
	protected void runRepeatedTasks() {
        synchronized (repeatedTasks)
        {
            for (Runnable r : repeatedTasks)
                r.run();
        }
    }

    /**
     * Get the average number of updates per second that this client is executing.
     *
     * @return The average number of updates per second that this client is executing.
     */
    public double getAverageUPS() { return 1.0d / clientLoop.getAverageRunTime(); }

	/**
	 * Send a login request.
	 */
	protected void sendLoginRequest() {
		loginTracker.setWaitingForResponse(true);
		Message message = generateLoginRequest(user);
		enqueueMessage(message);
	}

	/**
	 * The ClientLoop class is essentially what amounts to the output thread.
     *
     * @author Caleb Brinkman
     */

    private static class ClientLoop extends TimerTask
    {
        private static final int MAX_STORED_UPDATE_TIMES = 50;
        private static final int NANOS_TO_SECOND = 1000000000;
        private static final Logger LOGGER = Logger.getLogger(ClientLoop.class.getName());
        /** The Client for this loop. */
        private final Client client;
        private int updateCount;
        private long lastStart = System.nanoTime();
        private final long[] updateTimesNanos = new long[MAX_STORED_UPDATE_TIMES];

        /**
         * Construct a ClientLoop for the given client.
         *
         * @param client The client for this ClientLoop
         */
        ClientLoop(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            updateCount++;
            saveUpdateTime();
            client.runRepeatedTasks();
        }

        private void saveUpdateTime() {
            long newStart = System.nanoTime();
            long timeElapsed = newStart - lastStart;
            lastStart = newStart;
            updateTimesNanos[updateCount % updateTimesNanos.length] = timeElapsed;
        }

        public double getAverageRunTime() {
            double maxIndex = Math.min(updateCount, updateTimesNanos.length);
            double total = 0;
            for (int i = 0; i < maxIndex; i++)
            {
                total += updateTimesNanos[i];
			}
			return total / maxIndex / NANOS_TO_SECOND;
		}

	}

	void sendLogoutRequest() {
		loginTracker.setWaitingForResponse(true);
		Message message = generateLogoutRequest();
		enqueueMessage(message);
	}
}
