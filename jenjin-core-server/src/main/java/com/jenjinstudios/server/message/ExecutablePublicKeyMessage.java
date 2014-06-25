package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.net.ClientHandler;

/**
 * This class handles processing a PublicKeyMessage from the client.
 * @author Caleb Brinkman
 */
public class ExecutablePublicKeyMessage extends ServerExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutablePublicKeyMessage(ClientHandler handler, Message message) {
		super(handler, message);
	}

	@Override
	public void runDelayed() {
	}

	@Override
	public void runImmediate() {
		byte[] publicKeyBytes = (byte[]) getMessage().getArgument("key");
		Message aesMessage = getClientHandler().getMessageFactory().generateAESKeyMessage(publicKeyBytes);
		getClientHandler().queueOutgoingMessage(aesMessage);
	}

}
