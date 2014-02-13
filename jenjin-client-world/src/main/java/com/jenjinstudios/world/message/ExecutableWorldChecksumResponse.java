package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.WorldClient;

/**
 * Handles login responses from the server.
 * @author Caleb Brinkman
 */
public class ExecutableWorldChecksumResponse extends WorldClientExecutableMessage
{
	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableWorldChecksumResponse(WorldClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runSynced() {
	}

	@Override
	public void runASync() {
		getClient().setServerWorldFileChecksum((byte[]) getMessage().getArgument("checksum"));
		getClient().setHasReceivedWorldFileChecksum(true);
	}
}
