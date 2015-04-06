package com.jenjinstudios.server.net;

import java.security.KeyPair;

/**
 * Used to initialize Server objects.
 *
 * @author Caleb Brinkman
 */
public class ServerInit
{
	/** The default number of updates per second. */
	public static final int DEFAULT_UPS = 50;
	/** The default client handler class. */
	public static final Class DEFAULT_CLASS = ClientHandler.class;
	/** The default port number on which to listen. */
	public static final int DEFAULT_PORT = 51015;

	private int ups;
	private Class<? extends ClientHandler> handlerClass;
	private int port;
	private KeyPair keyPair;
	private Class<? extends ServerMessageContext> contextClass;

	/**
	 * Construct a new {@code ServerInit}.
	 */
	@SuppressWarnings("unchecked")
	public ServerInit() {
		ups = DEFAULT_UPS;
		handlerClass = DEFAULT_CLASS;
		port = DEFAULT_PORT;
	}

	/**
	 * Construct a copy of the given ServerInit.
	 *
	 * @param s The ServerInit to copy.
	 */
	public ServerInit(ServerInit s) {
		setUps(s.getUps());
		setPort(s.getPort());
		setHandlerClass(s.getHandlerClass());
		setKeyPair(s.getKeyPair());
	}

	/**
	 * Get the class of ClientHandler to be used by the server.
	 *
	 * @return The class of ClientHandler to be used by the server.
	 */
	public Class<? extends ClientHandler> getHandlerClass() { return handlerClass; }

	/**
	 * Set the class of ClientHanlder to be used by the server.
	 *
	 * @param handlerClass The class of the ClientHandler to be used by the server.
	 */
	public void setHandlerClass(Class<? extends ClientHandler> handlerClass) { this.handlerClass = handlerClass; }

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

	public Class<? extends ServerMessageContext> getContextClass() {
		return contextClass;
	}

	public void setContextClass(Class<? extends ServerMessageContext> contextClass) {
		this.contextClass = contextClass;
	}
}
