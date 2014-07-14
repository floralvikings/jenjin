package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.AuthClient;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Caleb Brinkman
 */
public class ExecutableLogoutResponseTest
{
	@Test
	public void testMessageExecution() {
		Message loginResponse = MessageRegistry.getInstance().createMessage("LogoutResponse");
		loginResponse.setArgument("success", true);

		AuthClient authClient = mock(AuthClient.class);

		ExecutableLogoutResponse executableLogoutResponse = new ExecutableLogoutResponse(authClient, loginResponse);
		executableLogoutResponse.runImmediate();
		executableLogoutResponse.runDelayed();

		verify(authClient).setLoggedIn(false);
	}
}
