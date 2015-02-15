package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.LoginTracker;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the ExecutableLogoutResponse class.
 * @author Caleb Brinkman
 */
public class ExecutableLogoutResponseTest
{
	@Test
	public void testMessageExecution() {
		Message loginResponse = MessageRegistry.getInstance().createMessage("LogoutResponse");
		loginResponse.setArgument("success", true);

		AuthClient authClient = mock(AuthClient.class);
		LoginTracker loginTracker = mock(LoginTracker.class);
		when(authClient.getLoginTracker()).thenReturn(loginTracker);

        ExecutableLogoutResponse response = new ExecutableLogoutResponse(authClient, loginResponse);
        response.runImmediate();
        response.runDelayed();

		verify(loginTracker).setLoggedIn(false);
	}
}
