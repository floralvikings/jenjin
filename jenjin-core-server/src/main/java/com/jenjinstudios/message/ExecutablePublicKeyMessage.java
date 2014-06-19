package com.jenjinstudios.message;

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
	public void runSynced() {
	}

	@Override
	public void runASync() {
		Message aesMessage = getClientHandler().getMessageFactory()
				.generateAESKeyMessage((byte[]) getMessage().getArgument("key"));

		// Send the AESKeyMessage
		getClientHandler().queueMessage(aesMessage);
	}

}
