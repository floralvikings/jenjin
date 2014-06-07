package com.jenjinstudios.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.Client;

import java.security.PublicKey;

/**
 * Used to generate messages for the Jenjin core client.
 * @author Caleb Brinkman
 */
public class ClientMessageFactory extends MessageFactory
{
	private final Client client;

	public ClientMessageFactory(Client client) {
		super(client);
		this.client = client;
	}

	public Message generatePublicKeyMessage(PublicKey publicKey) {
		Message publicKeyMessage = new Message(client, "PublicKeyMessage");
		publicKeyMessage.setArgument("key", publicKey.getEncoded());
		return publicKeyMessage;
	}

	public Message generateLogoutRequest() {return new Message(client, "LogoutRequest");}

	/**
	 * Generate a LoginRequest message.
	 * @return The LoginRequest message.
	 */
	public Message generateLoginRequest(String username, String password) {// Create the login request.
		Message loginRequest = new Message(client, "LoginRequest");
		loginRequest.setArgument("username", username);
		loginRequest.setArgument("password", password);
		return loginRequest;
	}
}
