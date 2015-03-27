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

public class Server extends Thread
{
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	private final int UPS;
	private final int PERIOD;
	private final Authenticator authenticator;
	private final ClientListener clientListener;
    private final Map<Integer, ClientHandler> clientHandlers;
    private final KeyPair rsaKeyPair;
	private ScheduledExecutorService loopTimer;
	private ServerUpdateTask serverUpdateTask = new ServerUpdateTask(this);

	protected Server(ServerInit initInfo, Authenticator authenticator) throws IOException, NoSuchMethodException {
		super("Server");
        LOGGER.log(Level.FINE, "Initializing Server.");
        UPS = initInfo.getUps();
        PERIOD = 1000 / UPS;
        clientListener = new ClientListener(getClass(), initInfo.getHandlerClass(), initInfo.getPort());
        clientHandlers = new TreeMap<>();
		rsaKeyPair = (initInfo.getKeyPair() == null) ? Connection.generateRSAKeyPair() : initInfo.getKeyPair();
		this.authenticator = authenticator;
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core Client/Server Messages", stream);
	}

	public ServerUpdateTask getServerUpdateTask() { return serverUpdateTask; }

    public void checkListenerForClients() {
		Iterable<ClientHandler> nc = clientListener.getNewClients();
		for (ClientHandler h : nc)
        {
            addClientHandler(h);
            h.start();
        }

    }

	public int getUps() { return UPS; }

	private void addClientHandler(ClientHandler h) {
		int nullIndex = 0;
        synchronized (clientHandlers)
        {
            while (clientHandlers.containsKey(nullIndex)) nullIndex++;
            clientHandlers.put(nullIndex, h);
        }
        h.setHandlerId(nullIndex);
        h.setRSAKeyPair(rsaKeyPair);
    }

	public void update() {
		synchronized (clientHandlers)
        {
            Set<Integer> integers = clientHandlers.keySet();
            for (int i : integers)
            {
                ClientHandler t = clientHandlers.get(i);
                if (t != null)
                {
                    t.update();
                }
            }
        }
    }

	public Authenticator getAuthenticator() { return authenticator; }

	@Override
    public void run() {
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

    protected void removeClient(ClientHandler handler) {
        synchronized (clientHandlers)
        {
            clientHandlers.remove(handler.getHandlerId());
        }
    }
}
