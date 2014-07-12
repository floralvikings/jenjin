package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableLoginResponseTest
{
	@Test
	public void testMessageExecution() {
		Message loginResponse = MessageRegistry.getInstance().createMessage("LoginResponse");
		loginResponse.setArgument("success", true);
		loginResponse.setArgument("loginTime", 12345l);

		ClientUser user = mock(ClientUser.class);
		AuthClient authClient = mock(AuthClient.class);
		when(authClient.isLoggedIn()).thenReturn(true);
		when(authClient.getUser()).thenReturn(user);

		ExecutableLoginResponse executableLoginResponse = new ExecutableLoginResponse(authClient, loginResponse);
		executableLoginResponse.runImmediate();
		executableLoginResponse.runDelayed();

		verify(authClient).setLoggedInTime(12345l);
		verify(authClient).setWaitingForLoginResponse(false);
	}
}
