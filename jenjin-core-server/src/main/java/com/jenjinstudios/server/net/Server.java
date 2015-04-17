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
	private final KeyPair rsaKeyPair;
	private ScheduledExecutorService loopTimer;
	private ServerUpdateTask serverUpdateTask;

	protected Server(ServerInit<T> initInfo, Authenticator authenticator) throws IOException {
		LOGGER.log(Level.FINE, "Initializing Server.");
        UPS = initInfo.getUps();
		connectionPool = new ConnectionPool<>(initInfo.getPort(), initInfo.getContextClass());
		connectionPool.addShutdownTask(new EmergencyLogoutTask<>());
		PERIOD = 1000 / UPS;
		rsaKeyPair = (initInfo.getKeyPair() == null) ? Connection.generateRSAKeyPair() : initInfo.getKeyPair();
		this.authenticator = authenticator;
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core Client/Server Messages", stream);
	}

	public ServerUpdateTask getServerUpdateTask() { return serverUpdateTask; }

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
		serverUpdateTask = new ServerUpdateTask(this);
		connectionPool.start();

		loopTimer = Executors.newSingleThreadScheduledExecutor(new ServerUpdateThreadFactory());
		loopTimer.scheduleAtFixedRate(serverUpdateTask, 0, PERIOD, TimeUnit.MILLISECONDS);
	}

	/**
	 * Shutdown all active connections and stop listening for new ones.
	 *
	 * @throws IOException If there's an exception when whutting down clients.
	 */
	public void shutdown() throws IOException {
		connectionPool.shutdown();

		if (loopTimer != null)
			loopTimer.shutdown();
	}

	/**
	 * Get the ConnectionPool that manages connections for this server.
	 *
	 * @return The ConnectionPool that manages connections for this server.
	 */
	protected ConnectionPool<T> getConnectionPool() { return connectionPool; }

}
