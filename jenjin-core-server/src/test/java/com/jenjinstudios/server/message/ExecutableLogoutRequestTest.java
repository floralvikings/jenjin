package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.net.AuthServer;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.sql.Authenticator;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class ExecutableLogoutRequestTest
{
	@Test
	public void testMessageExecution() throws Exception {
		Message logoutRequest = MessageRegistry.getInstance().createMessage("LogoutRequest");

		User user = new User();
		user.setUsername("foo");
		user.setUsername("bar");
		ClientHandler clientHandler = mock(ClientHandler.class);
		AuthServer server = mock(AuthServer.class);
		ServerMessageFactory serverMessageFactory = new ServerMessageFactory(clientHandler,
			  MessageRegistry.getInstance());
		Authenticator authenticator = mock(Authenticator.class);
		when(server.getAuthenticator()).thenReturn(authenticator);
		when(server.getCycleStartTime()).thenReturn(12345l);
		when(authenticator.logOutUser(Mockito.<String>any())).thenReturn(new User());
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageRegistry()).thenReturn(MessageRegistry.getInstance());
		when(clientHandler.getMessageFactory()).thenReturn(serverMessageFactory);
		when(clientHandler.getUser()).thenReturn(user);

		ExecutableLogoutRequest executableLogoutRequest = new ExecutableLogoutRequest(clientHandler, logoutRequest);
		executableLogoutRequest.runImmediate();
		executableLogoutRequest.runDelayed();

		verify(clientHandler).setUser(null);
	}
}
