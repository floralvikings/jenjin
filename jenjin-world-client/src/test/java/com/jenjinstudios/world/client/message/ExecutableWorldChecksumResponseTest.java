package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.ServerWorldFileTracker;
import com.jenjinstudios.world.client.WorldClient;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldChecksumResponseTest
{
	@Test
	public void testMessageExecution() {
		byte[] checksum = {1, 2, 3, 4, 5};
		Message message = mock(Message.class);
		when(message.getArgument("checksum")).thenReturn(checksum);

		WorldClient worldClient = mock(WorldClient.class);
		ServerWorldFileTracker serverWorldFileTracker = new ServerWorldFileTracker(worldClient, null);
		when(worldClient.getServerWorldFileTracker()).thenReturn(serverWorldFileTracker);

		ExecutableWorldChecksumResponse response = new ExecutableWorldChecksumResponse(worldClient, message);
		response.execute();

		assertEquals(serverWorldFileTracker.getChecksum(), checksum);
		assertFalse(serverWorldFileTracker.isWaitingForChecksum());
	}
}
