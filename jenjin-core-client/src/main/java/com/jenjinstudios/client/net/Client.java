package com.jenjinstudios.client.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;

import java.security.KeyPair;
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
    private static final int UPDATES_PER_SECOND = 60;
    /** The list of tasks that this client will execute each update cycle. */
    private final List<Runnable> repeatedTasks;
    /** The timer that manages the update loop. */
    private Timer sendMessagesTimer;
    private ClientLoop clientLoop = new ClientLoop(this);

    /**
     * Construct a new client and attempt to connect to the server over the specified port.
     *
     * @param messageIO The MessageIO used to send and recieve messages.
     */
    protected Client(MessageIO messageIO) {
        super(messageIO);
        repeatedTasks = new LinkedList<>();
    }

    /**
     * Generate a PingRequest message.
     *
     * @return The generated message.
     */
    public static Message generatePingRequest() {
        Message pingRequest = MessageRegistry.getInstance().createMessage("PingRequest");
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
        getMessageIO().queueOutgoingMessage(generatePingRequest());

        sendMessagesTimer = new Timer("Client Update Loop", false);
        int period = 1000 / UPDATES_PER_SECOND;
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

    /**
     * Get the average number of updates per second that this client is executing.
     *
     * @return The average number of updates per second that this client is executing.
     */
    public double getAverageUPS() { return 1d / clientLoop.getAverageRunTime(); }
}
