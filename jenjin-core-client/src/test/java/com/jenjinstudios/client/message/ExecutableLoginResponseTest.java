package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.client.net.LoginTracker;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Tests ExecutableLoginResponse.
 * @author Caleb Brinkman
 */
public class ExecutableLoginResponseTest
{
    /**
     * Test execution of the message.
     */
    @Test
	public void testMessageExecution() {
		Message loginResponse = MessageRegistry.getInstance().createMessage("LoginResponse");
		loginResponse.setArgument("success", true);
        loginResponse.setArgument("loginTime", 12345L);

		ClientUser user = mock(ClientUser.class);
		AuthClient authClient = mock(AuthClient.class);
		LoginTracker loginTracker = mock(LoginTracker.class);
		when(loginTracker.isLoggedIn()).thenReturn(true);
		when(loginTracker.getLoggedInTime()).thenReturn(12345l);
		when(authClient.getLoginTracker()).thenReturn(loginTracker);
		when(authClient.getUser()).thenReturn(user);

        ExecutableLoginResponse response = new ExecutableLoginResponse(authClient, loginResponse);
        response.runImmediate();
        response.runDelayed();

		verify(loginTracker).setLoggedInTime(12345l);
		Assert.assertEquals(loginTracker.getLoggedInTime(), 12345l);
	}
}
