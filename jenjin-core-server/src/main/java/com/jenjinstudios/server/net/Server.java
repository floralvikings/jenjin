package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.concurrency.ConnectionPool;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server<U extends User, C extends ServerMessageContext<U>>
{
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	private final ConnectionPool<C> connectionPool;
	private final int UPS;
	private final int PERIOD;
	private final Authenticator<U> authenticator;
	private final KeyPair rsaKeyPair;
	private ScheduledExecutorService loopTimer;

	protected Server(ServerInit<C> initInfo, Authenticator<U> authenticator) throws IOException {
		LOGGER.log(Level.FINE, "Initializing Server.");
		rsaKeyPair = (initInfo.getKeyPair() == null) ? Connection.generateRSAKeyPair() : initInfo.getKeyPair();
		this.authenticator = authenticator;
		UPS = initInfo.getUps();
		PERIOD = 1000 / UPS;
		connectionPool = new ConnectionPool<>(initInfo.getPort(), initInfo.getContextClass());
		connectionPool.addShutdownTask(new EmergencyLogoutTask<>());
		connectionPool.addConnectionAddedTask(connection -> {
			connection.setRSAKeyPair(rsaKeyPair);
			connection.getMessageContext().setAuthenticator(authenticator);
		});
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core Client/Server Messages", stream);
	}

	public int getUps() { return UPS; }

	public Authenticator getAuthenticator() { return authenticator; }

	/**
	 * Start listening for and maintaining connections.
	 */
	public void start() {
		connectionPool.start();

		loopTimer = Executors.newSingleThreadScheduledExecutor(new ServerUpdateThreadFactory());
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
	protected ConnectionPool<C> getConnectionPool() { return connectionPool; }

}
