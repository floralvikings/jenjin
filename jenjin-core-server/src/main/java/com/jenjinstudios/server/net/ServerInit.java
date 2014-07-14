package com.jenjinstudios.server.net;

import com.jenjinstudios.core.io.MessageRegistry;

/**
 * @author Caleb Brinkman
 */
public class ServerInit<T extends ClientHandler>
{
	private final MessageRegistry messageRegistry;
	private final int ups;
	private final ClientListenerInit<T> clientListenerInit;

	public ServerInit(MessageRegistry messageRegistry, int ups, ClientListenerInit<T> clientListenerInit) {

		this.messageRegistry = messageRegistry;
		this.ups = ups;
		this.clientListenerInit = clientListenerInit;
	}

	public int getUps() { return ups; }

	public MessageRegistry getMessageRegistry() { return messageRegistry; }

	public ClientListenerInit<T> getClientListenerInit() { return clientListenerInit; }
}
