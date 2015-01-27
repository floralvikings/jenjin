package com.jenjinstudios.server.net;

import java.io.InputStream;
import java.security.KeyPair;

/**
 * Used to initialize Server objects.
 *
 * @author Caleb Brinkman
 */
public class ServerInit<T extends ClientHandler> implements Cloneable
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

	public void setHandlerClass(Class<T> handlerClass) { this.handlerClass = handlerClass; }

	public int getPort() { return port; }

	public void setPort(int port) { this.port = port; }

	public int getUps() { return ups; }

	public void setUps(int ups) { this.ups = ups; }

	public KeyPair getKeyPair() { return keyPair; }

	public void setKeyPair(KeyPair keyPair) { this.keyPair = keyPair; }

	@Override
	public ServerInit<T> clone() {
		return null;
	}

	public static ServerInit readFromFile(String filename) {
		return null;
	}

	public static ServerInit readFromStream(InputStream stream) {
		return null;
	}
}
