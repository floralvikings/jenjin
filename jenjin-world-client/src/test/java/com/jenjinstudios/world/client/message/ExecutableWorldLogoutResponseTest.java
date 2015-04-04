package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.net.ClientMessageContext;
import com.jenjinstudios.client.net.LoginTracker;
import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;
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
		Message worldLogoutResponse = mock(Message.class);
		when(worldLogoutResponse.getArgument("success")).thenReturn(true);

		ClientMessageContext context = mock(ClientMessageContext.class);
		WorldClient worldClient = mock(WorldClient.class);
		LoginTracker loginTracker = mock(LoginTracker.class);
		when(context.getLoginTracker()).thenReturn(loginTracker);

		ExecutableMessage message = new ExecutableWorldLogoutResponse(worldClient, worldLogoutResponse, context);
		message.execute();

        verify(loginTracker).setLoggedIn(false);
    }
}
