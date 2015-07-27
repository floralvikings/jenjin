package com.jenjinstudios.client.net;

import com.jenjinstudios.core.connection.Connection;
import com.jenjinstudios.core.connection.ConnectionConfig;
import com.jenjinstudios.core.connection.ConnectionInstantiationException;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The base class for any client.  This class uses a similar system to the JGSA.
 *
 * @author Caleb Brinkman
 */
public class Client<T extends ClientMessageContext> extends Connection<T>
{
	private static final int UPDATES_PER_SECOND = 60;
	private final ScheduledExecutorService executorService;
	private final List<Runnable> repeatedTasks;
	private final ClientLoop clientLoop = new ClientLoop(this);

    /**
	 * Construct a new client with the given configuration, input stream, and output stream.
	 *
	 * @param config The connection configuration
	 * @throws ConnectionInstantiationException If there's an exception initializing the connection.
	 */
	public Client(ConnectionConfig<T> config) throws ConnectionInstantiationException
	{
		super(config);
		executorService = Executors.newSingleThreadScheduledExecutor();
		repeatedTasks = new LinkedList<>();
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
		executorService.shutdown();
	}

    @Override
    public void start() {
        // Finally, send a ping request to establish latency.
		enqueueMessage(generatePingRequest());

		int period = 1000 / UPDATES_PER_SECOND;
		executorService.scheduleWithFixedDelay(clientLoop, 0, period, TimeUnit.MILLISECONDS);

		super.start();
	}

	/**
	 * Get the LoginTracker managed by this client.
	 *
	 * @return The login tracker managed by this client.
	 */
	public LoginTracker getLoginTracker() { return getMessageContext().getLoginTracker(); }

	/** Run the repeated synchronized tasks. */
	protected void runRepeatedTasks() {
        synchronized (repeatedTasks)
        {
			repeatedTasks.forEach(Runnable::run);
		}
    }

    /**
     * Get the average number of updates per second that this client is executing.
     *
     * @return The average number of updates per second that this client is executing.
     */
    public double getAverageUPS() { return 1.0d / clientLoop.getAverageRunTime(); }

	/**
	 * The ClientLoop class is essentially what amounts to the output thread.
     *
     * @author Caleb Brinkman
     */
	private static class ClientLoop implements Runnable
	{
		private static final int MAX_STORED_UPDATE_TIMES = 50;
		private static final int NANOS_TO_SECOND = 1000000000;
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
}
