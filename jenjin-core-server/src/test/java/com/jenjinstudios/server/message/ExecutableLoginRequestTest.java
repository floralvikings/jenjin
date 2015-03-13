package com.jenjinstudios.server.message;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.authentication.LoginException;
import com.jenjinstudios.server.net.AuthServer;
import com.jenjinstudios.server.net.ClientHandler;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class ExecutableLoginRequestTest
{
	@Test
	@SuppressWarnings("unchecked")
	public void testMessageExecution() throws Exception {
		Message message = MessageRegistry.getInstance().createMessage("LoginRequest");
		message.setArgument("username", "foo");
		message.setArgument("password", "bar");

		BasicUser user = new BasicUser();
		user.setUsername("foo");
		user.setUsername("bar");
		user.setLoggedIn(true);
		ClientHandler clientHandler = mock(ClientHandler.class);
		AuthServer server = mock(AuthServer.class);
		ServerMessageFactory serverMessageFactory = new ServerMessageFactory();
		Authenticator authenticator = mock(Authenticator.class);
		MessageIO messageIO = mock(MessageIO.class);
		when(server.getAuthenticator()).thenReturn(authenticator);
		when(server.getCycleStartTime()).thenReturn(12345l);
		when(authenticator.logInUser("foo", "bar")).thenReturn(user);
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageFactory()).thenReturn(serverMessageFactory);
		when(clientHandler.getMessageIO()).thenReturn(messageIO);

		ExecutableLoginRequest executableLoginRequest = new ExecutableLoginRequest(clientHandler, message);
		executableLoginRequest.runImmediate();
		executableLoginRequest.runDelayed();

		Mockito.verify(clientHandler).setLoggedInTime(anyLong());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFailedLogin() throws Exception {
		Message message = MessageRegistry.getInstance().createMessage("LoginRequest");
		message.setArgument("username", "foo-dapoo");
		message.setArgument("password", "bar");

		BasicUser user = new BasicUser();
		user.setUsername("foo");
		user.setUsername("bar");
		user.setLoggedIn(true);
		ClientHandler clientHandler = mock(ClientHandler.class);
		AuthServer server = mock(AuthServer.class);
		ServerMessageFactory serverMessageFactory = new ServerMessageFactory();
		Authenticator authenticator = mock(Authenticator.class);
		MessageIO messageIO = mock(MessageIO.class);
		when(server.getAuthenticator()).thenReturn(authenticator);
		when(server.getCycleStartTime()).thenReturn(12345l);
		when(authenticator.logInUser("foo", "bar")).thenReturn(user);
		when(authenticator.logInUser("foo-dapoo", "bar")).thenThrow(new LoginException("Nope"));
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageFactory()).thenReturn(serverMessageFactory);
		when(clientHandler.getMessageIO()).thenReturn(messageIO);

		ExecutableLoginRequest executableLoginRequest = new ExecutableLoginRequest(clientHandler, message);
		executableLoginRequest.runImmediate();
		executableLoginRequest.runDelayed();

		Mockito.verify(clientHandler, Mockito.never()).setLoggedInTime(anyLong());
	}
}
