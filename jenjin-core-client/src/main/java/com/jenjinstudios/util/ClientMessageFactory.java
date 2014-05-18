package com.jenjinstudios.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.AuthClient;
import com.jenjinstudios.net.Client;
import com.jenjinstudios.net.Connection;

import java.security.PublicKey;

/** @author Caleb Brinkman */
public class ClientMessageFactory
{
	public static Message generatePublicKeyMessage(Client client, PublicKey publicKey) {
		Message publicKeyMessage = new Message(client, "PublicKeyMessage");
		publicKeyMessage.setArgument("key", publicKey.getEncoded());
		return publicKeyMessage;
	}

	public static Message generateLogoutRequest(AuthClient client) {return new Message(client, "LogoutRequest");}

	/**
	 * Generate a LoginRequest message.
	 * @return The LoginRequest message.
	 */
	public static Message generateLoginRequest(Connection conn, String username, String password) {// Create the login request.
		Message loginRequest = new Message(conn, "LoginRequest");
		loginRequest.setArgument("username", username);
		loginRequest.setArgument("password", password);
		return loginRequest;
	}
}
