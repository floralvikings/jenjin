package com.jenjinstudios.server.net;

import java.security.KeyPair;

/**
 * Used to initialize Server objects.
 *
 * @author Caleb Brinkman
 */
public class ServerInit<T extends ServerMessageContext>
{
	private static final int DEFAULT_UPS = 50;
	private static final int DEFAULT_PORT = 51015;

	private int ups;
	private int port;
	private KeyPair keyPair;
	private Class<? extends T> contextClass;

	/**
	 * Construct a new {@code ServerInit}.
	 */
	public ServerInit() {
		ups = DEFAULT_UPS;
		port = DEFAULT_PORT;
	}

	/**
	 * Construct a copy of the given ServerInit.
	 *
	 * @param init The ServerInit to copy.
	 */
	public ServerInit(ServerInit init) {
		this.ups = init.getUps();
		this.port = init.getPort();
		this.keyPair = init.getKeyPair();
	}

	/**
	 * Get the port number on which the server will listen.
	 *
	 * @return The port number on which the server will listen.
	 */
	public int getPort() { return port; }

	/**
	 * Set the port number on which the server will listen.
	 *
	 * @param port The port number on which the server will listen.
	 */
	public void setPort(int port) { this.port = port; }

	/**
	 * Get the number of updates per second that the server will run.
	 *
	 * @return The number of updates per second that the server will run.
	 */
	public int getUps() { return ups; }

	/**
	 * Set the number of updates per second that the server will run.
	 *
	 * @param ups The number of updates per second that the server will run.
	 */
	public void setUps(int ups) { this.ups = ups; }

	/**
	 * Get the KeyPair used by the server to encrypt and decrypt messages.
	 *
	 * @return The KeyPair used by the server to encrypt and decrypt messages.
	 */
	public KeyPair getKeyPair() { return keyPair; }

	/**
	 * Set the KeyPair used by the server to encrypt and decrypt messages.
	 *
	 * @param keyPair The KeyPair used by the server to encrypt and decrypt messages.
	 */
	public void setKeyPair(KeyPair keyPair) { this.keyPair = keyPair; }

	/**
	 * Get the class of the MessageContext that will be used by connections to the server.
	 *
	 * @return The context class.
	 */
	public Class<? extends T> getContextClass() {
		return contextClass;
	}

	/**
	 * Set the class of the MessageContext that will be used by connections to the server.
	 *
	 * @param contextClass The context class.
	 */
	public void setContextClass(Class<? extends T> contextClass) {
		this.contextClass = contextClass;
	}
}
