package com.jenjinstudios.jgsf.message;

import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.message.Message;

import java.io.IOException;

/**
 * Used to request a ping message.
 *
 * @author Caleb Brinkman
 */
public class ExecutablePingRequest extends ServerExecutableMessage
{
	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	public ExecutablePingRequest(ClientHandler handler, Message message)
	{
		super(handler, message);
	}

	/** Run the synced portion of this message. */
	@Override
	public void runSynced()
	{
	}

	/** Run asynchronous portion of this message. */
	@Override
	public void runASync()
	{
		Message pingResponse = new Message("PingResponse");
		pingResponse.setArgument("requestTimeNanos", getMessage().getArgument("requestTimeNanos"));
		try
		{
			// Try to force the message through immediately, ignoring queue and sync times.
			getClientHandler().forceMessage(pingResponse);
		} catch (IOException e)
		{
			// If that fails, queue it normally. This will return a ping time scewed by the server update cycle.
			getClientHandler().queueMessage(pingResponse);
		}
	}
}
