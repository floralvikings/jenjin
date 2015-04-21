package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.concurrency.ConnectionPool;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages a ConnectionPool which listens for incoming connections an executes tasks on them as dictated by the tasks
 * added; maintains the RSA key pair used to encrypt/decrypt messages, and the Authenticator used to authenticate
 * users.
 *
 * @param <U> The type of user that will be added in this server.
 * @param <C> The type of MessageContext that will be passed to messages received by connections on this server.
 */
public class Server<U extends User, C extends ServerMessageContext<U>>
{
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
	private final ConnectionPool<C> connectionPool;
	private final int ups;
	private final Authenticator<U> authenticator;
	private final KeyPair rsaKeyPair;

	/**
	 * Construct a new Server.
	 *
	 * @param initInfo The information used to initialize the server.
	 * @param authenticator The Authenticator used to authenticate users for this server.
	 *
	 * @throws IOException If there is an error registering messages.
	 */
	public Server(ServerInit<C> initInfo, Authenticator<U> authenticator) throws IOException {
		LOGGER.log(Level.FINE, "Initializing Server.");
		rsaKeyPair = (initInfo.getKeyPair() == null) ? Connection.generateRSAKeyPair() : initInfo.getKeyPair();
		this.authenticator = authenticator;
		ups = initInfo.getUps();
		connectionPool = new ConnectionPool<>(initInfo.getPort(), initInfo.getContextClass());
		connectionPool.addShutdownTask(new EmergencyLogoutTask<>());
		connectionPool.addConnectionAddedTask(connection -> {
			connection.setRSAKeyPair(rsaKeyPair);
			connection.getMessageContext().setAuthenticator(authenticator);
		});
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core Client/Server Messages", stream);
	}

	// TODO This should probably be removed, folded into World since that's the only time it's used.
	public int getUps() { return ups; }

	public Authenticator getAuthenticator() { return authenticator; }

	/**
	 * Start listening for and maintaining connections.
	 */
	public void start() {
		connectionPool.start();
	}

	/**
	 * Shutdown all active connections and stop listening for new ones.
	 *
	 * @throws IOException If there's an exception when whutting down clients.
	 */
	public void shutdown() throws IOException {
		connectionPool.shutdown();
	}

	/**
	 * Get the ConnectionPool that manages connections for this server.
	 *
	 * @return The ConnectionPool that manages connections for this server.
	 */
	protected ConnectionPool<C> getConnectionPool() { return connectionPool; }

}
