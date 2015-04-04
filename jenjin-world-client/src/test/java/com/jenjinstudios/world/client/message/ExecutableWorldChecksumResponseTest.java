package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldFileTracker;
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
		WorldFileTracker worldFileTracker = new WorldFileTracker(null);
		when(worldClient.getWorldFileTracker()).thenReturn(worldFileTracker);

		ExecutableWorldChecksumResponse response = new ExecutableWorldChecksumResponse(worldClient, message, null);
		response.execute();

		assertEquals(worldFileTracker.getChecksum(), checksum);
		assertFalse(worldFileTracker.isWaitingForChecksum());
	}
}
