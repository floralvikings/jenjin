package com.jenjinstudios.core.event;

import com.jenjinstudios.core.io.Message;

/**
 * Notified when a MessageInputStream receives a message.
 *
 * @author Caleb Brinkman
 */
public interface MessageReceivedListener extends EventListener
{
	public void onMessageReceived(Message message);
}
