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
	/** The client for which this message factory works. */
	private final Client client;

	/**
	 * Construct a new ClientMessageFactory.
	 * @param client The client for which the message factory works.
	 */
	public ClientMessageFactory(Client client) {
		super(client);
		this.client = client;
	}

	/**
	 * Generate a PublicKeyMessage with the given public key.
	 * @param publicKey The public key.
	 * @return The PublicKeyMessage.
	 */
	public Message generatePublicKeyMessage(PublicKey publicKey) {
		Message publicKeyMessage = new Message(client, "PublicKeyMessage");
		publicKeyMessage.setArgument("key", publicKey.getEncoded());
		return publicKeyMessage;
	}

	/**
	 * Generate a LogoutRequest message.
	 * @return The LogoutRequestMessage.
	 */
	public Message generateLogoutRequest() {return new Message(client, "LogoutRequest");}

	/**
	 * Generate a LoginRequest message.  This message will be encrypted if possible.
	 * @param username The user's plaintext username.
	 * @param password The user's plaintext password.
	 * @return The LoginRequest message.
	 */
	public Message generateLoginRequest(String username, String password) {// Create the login request.
		Message loginRequest = new Message(client, "LoginRequest");
		loginRequest.setArgument("username", username);
		loginRequest.setArgument("password", password);
		return loginRequest;
	}
}
