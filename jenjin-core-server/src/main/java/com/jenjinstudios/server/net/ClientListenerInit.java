package com.jenjinstudios.server.net;

/**
 * @author Caleb Brinkman
 */
public class ClientListenerInit<T extends ClientHandler>
{
	private final Class<T> handlerClass;
	private final int port;

	public ClientListenerInit(Class<T> handlerClass, int port) {

		this.handlerClass = handlerClass;
		this.port = port;
	}

	public Class<T> getHandlerClass() { return handlerClass; }

	public int getPort() { return port; }
}
