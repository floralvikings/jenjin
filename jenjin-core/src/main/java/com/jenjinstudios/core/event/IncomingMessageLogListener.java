package com.jenjinstudios.core.event;

import com.jenjinstudios.core.io.Message;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs incoming messages.
 *
 * @author Caleb Brinkman
 */
public class IncomingMessageLogListener implements MessageReceivedListener
{
	private static final Logger LOGGER = Logger.getLogger(IncomingMessageLogListener.class.getName());

	@Override
	public void onMessageReceived(Message message) {
		LOGGER.log(Level.FINEST, "Reading message: {0}", message);
	}
}
