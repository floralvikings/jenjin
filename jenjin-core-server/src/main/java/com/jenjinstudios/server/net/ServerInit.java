package com.jenjinstudios.server.net;

import java.security.KeyPair;

/**
 * Used to initialize Server objects.
 *
 * @author Caleb Brinkman
 */
public class ServerInit<T extends ClientHandler>
{
	private int ups;
	private Class<T> handlerClass;
	private int port;
	private KeyPair keyPair;

	public ServerInit(int ups, Class<T> handlerClass, int port) {
		this.ups = ups;
		this.handlerClass = handlerClass;
		this.port = port;
	}

	public Class<T> getHandlerClass() { return handlerClass; }

	public int getPort() { return port; }

	public int getUps() { return ups; }

	public KeyPair getKeyPair() { return keyPair; }

	public void setKeyPair(KeyPair keyPair) { this.keyPair = keyPair; }
}
