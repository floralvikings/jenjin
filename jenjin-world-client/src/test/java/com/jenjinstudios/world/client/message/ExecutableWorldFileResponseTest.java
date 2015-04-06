package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.client.WorldFileTracker;
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
		Message message = mock(Message.class);
		when(message.getArgument("fileBytes")).thenReturn(fileBytes);

		WorldClient worldClient = mock(WorldClient.class);
		WorldFileTracker worldFileTracker = new WorldFileTracker();
		WorldClientMessageContext context = mock(WorldClientMessageContext.class);
		when(context.getWorldFileTracker()).thenReturn(worldFileTracker);

		ExecutableWorldFileResponse response = new ExecutableWorldFileResponse(message, context);
		response.execute();

		assertEquals(worldFileTracker.getBytes(), fileBytes);
		assertFalse(worldFileTracker.isWaitingForFile());
	}
}
