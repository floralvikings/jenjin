package com.jenjinstudios.core;

import com.jenjinstudios.core.io.MessageStreamPair;

/**
 * Contains methods used to generate, set and send RSA Keys over a Connection.
 */
public class EncryptedConnection extends Connection
{

	/**
	 * Construct a new {@code Connection} that utilizes the specified {@code MessageIO} to read and write messages.
	 *
	 * @param streams The {@code MessageIO} containing streams used to read and write messages.
	 */
	public EncryptedConnection(MessageStreamPair streams) {
		super(streams);
	}

}
