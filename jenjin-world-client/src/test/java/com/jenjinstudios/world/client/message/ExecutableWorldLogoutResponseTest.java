package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.client.WorldClient;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutResponseTest
{
	@Test
	public void testMessageExecution() throws Exception {
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message worldLogoutResponse = messageRegistry.createMessage("WorldLogoutResponse");
		worldLogoutResponse.setArgument("success", true);

		WorldClient worldClient = mock(WorldClient.class);

		ExecutableWorldLogoutResponse message = new ExecutableWorldLogoutResponse(worldClient, worldLogoutResponse);
		message.runImmediate();
		message.runDelayed();

		verify(worldClient).setWaitingForLogoutResponse(false);
		verify(worldClient).setLoggedIn(false);
	}
}
