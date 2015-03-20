package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.net.AuthServer;
import com.jenjinstudios.server.net.ClientHandler;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableLogoutRequestTest
{
	@SuppressWarnings("unchecked")
	@Test
	public void testMessageExecution() throws Exception {
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message logoutRequest = messageRegistry.createMessage("LogoutRequest");

		User user = new BasicUser();
		user.setUsername("foo");
		user.setUsername("bar");
		ClientHandler clientHandler = mock(ClientHandler.class);
		AuthServer server = mock(AuthServer.class);
		Authenticator<BasicUser> authenticator = mock(Authenticator.class);
		when(server.getAuthenticator()).thenReturn(authenticator);
		when(server.getCycleStartTime()).thenReturn(12345l);
		when(authenticator.logOutUser(Mockito.<String>any())).thenReturn(new BasicUser());
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getUser()).thenReturn(user);

		ExecutableLogoutRequest executableLogoutRequest = new ExecutableLogoutRequest(clientHandler, logoutRequest);
		executableLogoutRequest.runImmediate();
		executableLogoutRequest.runDelayed();

		verify(clientHandler).setUser(null);
	}
}
