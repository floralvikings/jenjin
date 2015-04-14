package com.jenjinstudios.server.net;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.MessageStreamPair;

public class ClientHandler<T extends ServerMessageContext> extends Connection<T>
{

	public ClientHandler(MessageStreamPair messageStreamPair, T context) {
		super(messageStreamPair, context);
		getMessageContext().setName("ClientHandler " + getId());
	}
}
