package com.jenjinstudios.core.event;

import com.jenjinstudios.core.io.Message;

/**
 * Notified when a MessageInputStream receives a message.
 *
 * @author Caleb Brinkman
 */
public interface MessageReceivedListener extends EventListener
{
	/**
	 * Called when a message has been received.
	 *
	 * @param message The message that was received.
	 */
	public void onMessageReceived(Message message);
}
