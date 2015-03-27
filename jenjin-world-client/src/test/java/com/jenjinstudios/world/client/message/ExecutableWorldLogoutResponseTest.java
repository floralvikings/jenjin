package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.net.AuthClient;
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
		MessageRegistry messageRegistry = MessageRegistry.getGlobalRegistry();
		Message worldLogoutResponse = mock(Message.class);
		when(worldLogoutResponse.getArgument("success")).thenReturn(true);

        WorldClient worldClient = mock(WorldClient.class);
        AuthClient.LoginTracker loginTracker = mock(AuthClient.LoginTracker.class);
        when(worldClient.getLoginTracker()).thenReturn(loginTracker);

        ExecutableWorldLogoutResponse message = new ExecutableWorldLogoutResponse(worldClient, worldLogoutResponse);
		message.execute();

        verify(loginTracker).setLoggedIn(false);
    }
}
