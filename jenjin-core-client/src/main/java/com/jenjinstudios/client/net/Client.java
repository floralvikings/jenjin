package com.jenjinstudios.client.net;

import com.jenjinstudios.client.message.ClientMessageFactory;
import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.util.MessageFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

/**
 * The base class for any client.  This class uses a similar system to the JGSA.
 *
 * @author Caleb Brinkman
 */
public class Client extends Connection
{
	/** The list of tasks that this client will execute each update cycle. */
	private final List<Runnable> repeatedTasks;
	/** The timer that manages the update loop. */
	private Timer sendMessagesTimer;
	private ClientLoop clientLoop = new ClientLoop(this);

	/**
	 * Construct a new client and attempt to connect to the server over the specified port.
	 */
	protected Client(MessageIO messageIO) {
		super(messageIO);
		repeatedTasks = new LinkedList<>();
		/* The message factory used by this client. */
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
		int period = 1000 / 60;
		// Finally, send a ping request to establish latency.
		getMessageIO().queueOutgoingMessage(MessageFactory.generatePingRequest());

		sendMessagesTimer = new Timer("Client Update Loop", false);
		sendMessagesTimer.scheduleAtFixedRate(clientLoop, 0, period);

		super.start();
	}

	/** Run the repeated synchronized tasks. */
	protected void runRepeatedTasks() {
		synchronized (repeatedTasks)
		{
			for (Runnable r : repeatedTasks)
				r.run();
		}
	}

	public double getAverageUPS() { return 1d / clientLoop.getAverageRunTime(); }
}
