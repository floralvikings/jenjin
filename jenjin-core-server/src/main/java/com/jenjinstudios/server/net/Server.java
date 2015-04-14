package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.Authenticator;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server<T extends ClientHandler<? extends ServerMessageContext>>
{
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	private final int UPS;
	private final int PERIOD;
	private final Authenticator authenticator;
	private final ClientListener<T> clientListener;
	private final Map<Integer, T> clientHandlers;
	private final KeyPair rsaKeyPair;
	private ScheduledExecutorService loopTimer;
	private ServerUpdateTask serverUpdateTask;

	protected Server(ServerInit initInfo, Authenticator authenticator) throws IOException, NoSuchMethodException {
        LOGGER.log(Level.FINE, "Initializing Server.");
        UPS = initInfo.getUps();
        PERIOD = 1000 / UPS;
		Class<? extends Server> serverClass = getClass();
		Class handlerClass = initInfo.getHandlerClass();
		Class<? extends ServerMessageContext> contextClass = initInfo.getContextClass();
		//noinspection unchecked
		clientListener = new ClientListener<>(serverClass, handlerClass, contextClass, initInfo.getPort());
		clientHandlers = new TreeMap<>();
		rsaKeyPair = (initInfo.getKeyPair() == null) ? Connection.generateRSAKeyPair() : initInfo
			  .getKeyPair();
		this.authenticator = authenticator;
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core Client/Server Messages", stream);
	}

	public ServerUpdateTask getServerUpdateTask() { return serverUpdateTask; }

    public void checkListenerForClients() {
		Iterable<T> nc = clientListener.getNewClients();
		for (T h : nc)
		{
            addClientHandler(h);
            h.start();
        }

    }

	public int getUps() { return UPS; }

	private void addClientHandler(T h) {
		int nullIndex = 0;
        synchronized (clientHandlers)
        {
            while (clientHandlers.containsKey(nullIndex)) nullIndex++;
            clientHandlers.put(nullIndex, h);
        }
        h.setHandlerId(nullIndex);
		clientHandlerAdded(h);
	}

	protected void clientHandlerAdded(T h) {
		h.setRSAKeyPair(rsaKeyPair);
		//noinspection unchecked
		h.getMessageContext().setAuthenticator(authenticator);
	}

	public Authenticator getAuthenticator() { return authenticator; }

	/**
	 * Start listening for and maintaining connections.
	 */
	public void start() {
		clientListener.startListening(this);

		serverUpdateTask = new ServerUpdateTask(this);

		loopTimer = Executors.newSingleThreadScheduledExecutor(new ServerUpdateThreadFactory());
		loopTimer.scheduleAtFixedRate(serverUpdateTask, 0, PERIOD, TimeUnit.MILLISECONDS);
	}

	public void shutdown() throws IOException {
		synchronized (clientHandlers)
        {
            Set<Integer> integers = clientHandlers.keySet();
            for (int i : integers)
            {
                ClientHandler t = clientHandlers.get(i);
                if (t != null)
                {
                    t.shutdown();
                }
            }
        }
        clientListener.stopListening();

		if (loopTimer != null)
			loopTimer.shutdown();
	}

	protected void removeClient(T handler) {
		synchronized (clientHandlers)
        {
            clientHandlers.remove(handler.getHandlerId());
        }
    }
}
