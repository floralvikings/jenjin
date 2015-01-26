package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.net.LoginTracker;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.client.WorldClient;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutResponseTest
{
	@Test
	public void testMessageExecution() throws Exception {
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message worldLogoutResponse = messageRegistry.createMessage("LogoutResponse");
		worldLogoutResponse.setArgument("success", true);

		WorldClient worldClient = mock(WorldClient.class);
		LoginTracker loginTracker = mock(LoginTracker.class);
		when(worldClient.getLoginTracker()).thenReturn(loginTracker);

		ExecutableWorldLogoutResponse message = new ExecutableWorldLogoutResponse(worldClient, worldLogoutResponse);
		message.runImmediate();
		message.runDelayed();

		verify(loginTracker).setLoggedIn(false);
	}
}
