package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
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
        Message loginResponse = MessageRegistry.getInstance().createMessage("LogoutResponse");
        loginResponse.setArgument("success", true);

        AuthClient authClient = mock(AuthClient.class);
        AuthClient.LoginTracker loginTracker = mock(AuthClient.LoginTracker.class);
        when(authClient.getLoginTracker()).thenReturn(loginTracker);

        ExecutableLogoutResponse response = new ExecutableLogoutResponse(authClient, loginResponse);
        response.runImmediate();
        response.runDelayed();

        verify(loginTracker).setLoggedIn(false);
    }
}
