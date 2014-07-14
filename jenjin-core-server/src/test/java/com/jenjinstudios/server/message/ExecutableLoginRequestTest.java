package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.net.AuthServer;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.sql.Authenticator;
import com.jenjinstudios.server.sql.LoginException;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableLoginRequestTest
{
	@Test
	public void testMessageExecution() throws Exception {
		Message message = MessageRegistry.getInstance().createMessage("LoginRequest");
		message.setArgument("username", "foo");
		message.setArgument("password", "bar");

		User user = new User();
		user.setUsername("foo");
		user.setUsername("bar");
		user.setLoggedIn(true);
		ClientHandler clientHandler = mock(ClientHandler.class);
		AuthServer server = mock(AuthServer.class);
		ServerMessageFactory serverMessageFactory = new ServerMessageFactory(clientHandler,
			  MessageRegistry.getInstance());
		Authenticator authenticator = mock(Authenticator.class);
		when(server.getAuthenticator()).thenReturn(authenticator);
		when(server.getCycleStartTime()).thenReturn(12345l);
		when(authenticator.logInUser("foo", "bar")).thenReturn(user);
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageRegistry()).thenReturn(MessageRegistry.getInstance());
		when(clientHandler.getMessageFactory()).thenReturn(serverMessageFactory);

		ExecutableLoginRequest executableLoginRequest = new ExecutableLoginRequest(clientHandler, message);
		executableLoginRequest.runImmediate();
		executableLoginRequest.runDelayed();

		verify(server).associateUsernameWithClientHandler("foo", clientHandler);
	}

	@Test
	public void testFailedLogin() throws Exception {
		Message message = MessageRegistry.getInstance().createMessage("LoginRequest");
		message.setArgument("username", "foo-dapoo");
		message.setArgument("password", "bar");

		User user = new User();
		user.setUsername("foo");
		user.setUsername("bar");
		user.setLoggedIn(true);
		ClientHandler clientHandler = mock(ClientHandler.class);
		AuthServer server = mock(AuthServer.class);
		ServerMessageFactory serverMessageFactory = new ServerMessageFactory(clientHandler,
			  MessageRegistry.getInstance());
		Authenticator authenticator = mock(Authenticator.class);
		when(server.getAuthenticator()).thenReturn(authenticator);
		when(server.getCycleStartTime()).thenReturn(12345l);
		when(authenticator.logInUser("foo", "bar")).thenReturn(user);
		when(authenticator.logInUser("foo-dapoo", "bar")).thenThrow(new LoginException("Nope"));
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageRegistry()).thenReturn(MessageRegistry.getInstance());
		when(clientHandler.getMessageFactory()).thenReturn(serverMessageFactory);

		ExecutableLoginRequest executableLoginRequest = new ExecutableLoginRequest(clientHandler, message);
		executableLoginRequest.runImmediate();
		executableLoginRequest.runDelayed();

		verify(server, times(0)).associateUsernameWithClientHandler("foo-dapoo", clientHandler);
	}
}
