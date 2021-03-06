package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
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
public class ExecutableWorldFileResponseTest
{
	@Test
	public void testMessageExecution() {
		byte[] fileBytes = {1, 2, 3, 4, 5};
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message message = messageRegistry.createMessage("WorldFileResponse");
		message.setArgument("fileBytes", fileBytes);

		WorldClient worldClient = mock(WorldClient.class);
		ServerWorldFileTracker serverWorldFileTracker = new ServerWorldFileTracker(worldClient, null);
		when(worldClient.getServerWorldFileTracker()).thenReturn(serverWorldFileTracker);

		ExecutableWorldFileResponse response = new ExecutableWorldFileResponse(worldClient, message);
		response.runImmediate();
		response.runDelayed();

		assertEquals(serverWorldFileTracker.getBytes(), fileBytes);
		assertFalse(serverWorldFileTracker.isWaitingForFile());
	}
}
