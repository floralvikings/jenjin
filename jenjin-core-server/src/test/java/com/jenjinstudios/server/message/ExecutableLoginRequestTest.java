package com.jenjinstudios.server.message;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.Server;
import com.jenjinstudios.server.net.ServerUpdateTask;
import org.mockito.Mockito;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class ExecutableLoginRequestTest
{
	@BeforeClass
	public void registerMessages() {
		MessageRegistry.getInstance().register("Core Message Registry",
			  getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/server/Messages.xml"));
	}

	/**
	 * Clear the message registry.
	 */
	@AfterClass
	public void clearMessageRegistry() {
		MessageRegistry.getInstance().clear();
	}

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
		Server server = mock(Server.class);
		Authenticator<BasicUser> authenticator = mock(Authenticator.class);
		MessageIO messageIO = mock(MessageIO.class);
		when(server.getAuthenticator()).thenReturn(authenticator);
		ServerUpdateTask serverUpdateTask = mock(ServerUpdateTask.class);
		when(server.getServerUpdateTask()).thenReturn(serverUpdateTask);
		when(serverUpdateTask.getCycleStartTime()).thenReturn(12345L);
		when(authenticator.logInUser("foo", "bar")).thenReturn(user);
		when(clientHandler.getServer()).thenReturn(server);
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
		Server server = mock(Server.class);
		Authenticator<BasicUser> authenticator = mock(Authenticator.class);
		MessageIO messageIO = mock(MessageIO.class);
		when(server.getAuthenticator()).thenReturn(authenticator);
		ServerUpdateTask serverUpdateTask = mock(ServerUpdateTask.class);
		when(server.getServerUpdateTask()).thenReturn(serverUpdateTask);
		when(serverUpdateTask.getCycleStartTime()).thenReturn(12345L);
		when(authenticator.logInUser("foo", "bar")).thenReturn(user);
		when(authenticator.logInUser("foo-dapoo", "bar")).thenThrow(new AuthenticationException("Nope"));
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageIO()).thenReturn(messageIO);

		ExecutableLoginRequest executableLoginRequest = new ExecutableLoginRequest(clientHandler, message);
		executableLoginRequest.runImmediate();
		executableLoginRequest.runDelayed();

		Mockito.verify(clientHandler, Mockito.never()).setLoggedInTime(anyLong());
	}
}
