package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.concurrency.ConnectionPool;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server<T extends ServerMessageContext>
{
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	private final ConnectionPool<T> connectionPool;
	private final int UPS;
	private final int PERIOD;
	private final Authenticator authenticator;
	private final ClientListener clientListener;
	private final KeyPair rsaKeyPair;
	private ScheduledExecutorService loopTimer;
	private ServerUpdateTask serverUpdateTask;

	protected Server(ServerInit initInfo, Authenticator authenticator) throws IOException, NoSuchMethodException {
        LOGGER.log(Level.FINE, "Initializing Server.");
        UPS = initInfo.getUps();
		connectionPool = new ConnectionPool<>();
		connectionPool.addShutdownTask(new EmergencyLogoutTask<T>());
		PERIOD = 1000 / UPS;
		Class<? extends ServerMessageContext> contextClass = initInfo.getContextClass();
		//noinspection unchecked
		clientListener = new ClientListener(contextClass, initInfo.getPort());
		rsaKeyPair = (initInfo.getKeyPair() == null) ? Connection.generateRSAKeyPair() : initInfo.getKeyPair();
		this.authenticator = authenticator;
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core Client/Server Messages", stream);
	}

	public ServerUpdateTask getServerUpdateTask() { return serverUpdateTask; }

    public void checkListenerForClients() {
		Iterable<Connection> nc = clientListener.getNewClients();
		for (Connection h : nc)
		{
			connectionPool.addConnection(h);
			clientHandlerAdded(h);
			h.start();
        }

    }

	public int getUps() { return UPS; }

	protected void clientHandlerAdded(Connection<? extends T> h) {
		h.setRSAKeyPair(rsaKeyPair);
		//noinspection unchecked
		h.getMessageContext().setAuthenticator(authenticator);
	}

	public Authenticator getAuthenticator() { return authenticator; }

	/**
	 * Start listening for and maintaining connections.
	 */
	public void start() {
		clientListener.startListening();

		serverUpdateTask = new ServerUpdateTask(this);

		loopTimer = Executors.newSingleThreadScheduledExecutor(new ServerUpdateThreadFactory());
		loopTimer.scheduleAtFixedRate(serverUpdateTask, 0, PERIOD, TimeUnit.MILLISECONDS);
	}

	public void shutdown() throws IOException {
		connectionPool.shutdown();
		clientListener.stopListening();

		if (loopTimer != null)
			loopTimer.shutdown();
	}

	protected ConnectionPool<T> getConnectionPool() { return connectionPool; }

}
