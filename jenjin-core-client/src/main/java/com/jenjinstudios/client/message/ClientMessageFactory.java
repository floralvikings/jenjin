package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.util.MessageFactory;

import java.security.PublicKey;

/**
 * Used to generate messages for the Jenjin core client.
 * @author Caleb Brinkman
 */
public class ClientMessageFactory extends MessageFactory
{

	/**
	 * Construct a new ClientMessageFactory.
	 */
	public ClientMessageFactory(MessageRegistry messageRegistry) {
		super(messageRegistry);
	}

	/**
	 * Generate a PublicKeyMessage with the given public key.
	 * @param publicKey The public key.
	 * @return The PublicKeyMessage.
	 */
	public Message generatePublicKeyMessage(PublicKey publicKey) {
		Message publicKeyMessage = MessageRegistry.getInstance().createMessage("PublicKeyMessage");
		publicKeyMessage.setArgument("key", publicKey.getEncoded());
		return publicKeyMessage;
	}

	/**
	 * Generate a LogoutRequest message.
	 * @return The LogoutRequestMessage.
	 */
	public Message generateLogoutRequest() {return getMessageRegistry().createMessage("LogoutRequest");}

	/**
	 * Generate a LoginRequest message.  This message will be encrypted if possible.
	 * @return The LoginRequest message.
	 */
	public Message generateLoginRequest(ClientUser user) {// Create the login request.
		Message loginRequest = getMessageRegistry().createMessage("LoginRequest");
		loginRequest.setArgument("username", user.getUsername());
		loginRequest.setArgument("password", user.getPassword());
		return loginRequest;
	}
}
