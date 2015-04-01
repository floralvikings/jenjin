package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.AuthClient.LoginTracker;
import com.jenjinstudios.core.io.Message;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the ExecutableLogoutResponse class.
 *
 * @author Caleb Brinkman
 */
public class ExecutableLogoutResponseTest
{
    /**
     * Test the execution of the ExecutableLogoutResponse.
     */
    @Test
    public void testMessageExecution() {
		Message loginResponse = mock(Message.class);
		when(loginResponse.getArgument("success")).thenReturn(true);

        AuthClient authClient = mock(AuthClient.class);
		LoginTracker loginTracker = mock(LoginTracker.class);
		when(authClient.getLoginTracker()).thenReturn(loginTracker);

		ExecutableLogoutResponse response = new ExecutableLogoutResponse(authClient, loginResponse, null);
		response.execute();

        verify(loginTracker).setLoggedIn(false);
    }
}
