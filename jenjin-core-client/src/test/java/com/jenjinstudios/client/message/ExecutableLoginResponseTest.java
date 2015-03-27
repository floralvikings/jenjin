package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.AuthClient.LoginTracker;
import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.io.Message;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Tests ExecutableLoginResponse.
 *
 * @author Caleb Brinkman
 */
public class ExecutableLoginResponseTest
{
    /**
     * Test execution of the message.
     */
    @Test
    public void testMessageExecution() {
		Message loginResponse = mock(Message.class);
		when(loginResponse.getArgument("success")).thenReturn(true);
		when(loginResponse.getArgument("loginTime")).thenReturn(12345L);

        ClientUser user = mock(ClientUser.class);
        AuthClient authClient = mock(AuthClient.class);
		LoginTracker loginTracker = mock(LoginTracker.class);
		when(loginTracker.isLoggedIn()).thenReturn(true);
        when(loginTracker.getLoggedInTime()).thenReturn(12345L);
        when(authClient.getLoginTracker()).thenReturn(loginTracker);
        when(authClient.getUser()).thenReturn(user);

        ExecutableLoginResponse response = new ExecutableLoginResponse(authClient, loginResponse);
        response.runImmediate();

        verify(loginTracker).setLoggedInTime(12345L);
        Assert.assertEquals(loginTracker.getLoggedInTime(), 12345L, "Login time was not as expected.");
    }
}
