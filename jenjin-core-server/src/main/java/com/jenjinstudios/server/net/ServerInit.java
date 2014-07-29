package com.jenjinstudios.server.net;

/**
 * @author Caleb Brinkman
 */
public class ServerInit<T extends ClientHandler>
{
	private final int ups;
	private final ClientListenerInit<T> clientListenerInit;

	public ServerInit(int ups, ClientListenerInit<T> clientListenerInit) {
		this.ups = ups;
		this.clientListenerInit = clientListenerInit;
	}

	public int getUps() { return ups; }

	public ClientListenerInit<T> getClientListenerInit() { return clientListenerInit; }
}
