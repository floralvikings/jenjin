package com.jenjinstudios.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.ClientHandler;
import com.jenjinstudios.util.ServerMessageFactory;

import java.io.IOException;

/**
 * Used to request a ping message.
 * @author Caleb Brinkman
 */
public class ExecutablePingRequest extends ServerExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutablePingRequest(ClientHandler handler, Message message) {
		super(handler, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runSynced() {
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runASync() {
		ClientHandler clientHandler = getClientHandler();
		long requestTimeNanos = (long)getMessage().getArgument("requestTimeNanos");

		Message pingResponse = ServerMessageFactory.generatePingResponse(clientHandler, requestTimeNanos);
		try
		{
			// Try to force the message through immediately, ignoring queue and sync times.
			clientHandler.forceMessage(pingResponse);
		} catch (IOException e)
		{
			// If that fails, queue it normally. This will return a ping time scewed by the server update cycle.
			clientHandler.queueMessage(pingResponse);
		}
	}

}
