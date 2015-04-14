package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.net.ServerMessageContext;
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
		MessageRegistry.getGlobalRegistry().register("Core Message Registry",
			  getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/server/Messages.xml"));
	}

	/**
	 * Clear the message registry.
	 */
	@AfterClass
	public void clearMessageRegistry() {
		MessageRegistry.getGlobalRegistry().clear();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMessageExecution() throws Exception {
		Message message = MessageRegistry.getGlobalRegistry().createMessage("LoginRequest");
		message.setArgument("username", "foo");
		message.setArgument("password", "bar");

		BasicUser user = new BasicUser();
		user.setUsername("foo");
		user.setUsername("bar");
		user.setLoggedIn(true);
		Authenticator<BasicUser> authenticator = mock(Authenticator.class);
		ServerMessageContext context = mock(ServerMessageContext.class);
		when(context.getAuthenticator()).thenReturn(authenticator);
		when(authenticator.logInUser("foo", "bar")).thenReturn(user);

		ExecutableLoginRequest exec = new ExecutableLoginRequest(message, context);
		exec.execute();

		Mockito.verify(context).setLoggedInTime(anyLong());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFailedLogin() throws Exception {
		Message message = MessageRegistry.getGlobalRegistry().createMessage("LoginRequest");
		message.setArgument("username", "foo-dapoo");
		message.setArgument("password", "bar");

		BasicUser user = new BasicUser();
		user.setUsername("foo");
		user.setUsername("bar");
		user.setLoggedIn(true);
		Authenticator<BasicUser> authenticator = mock(Authenticator.class);
		ServerMessageContext context = mock(ServerMessageContext.class);
		when(context.getAuthenticator()).thenReturn(authenticator);
		when(authenticator.logInUser("foo", "bar")).thenReturn(user);
		when(authenticator.logInUser("foo-dapoo", "bar")).thenThrow(new AuthenticationException("Nope"));

		ExecutableLoginRequest exec = new ExecutableLoginRequest(message, context);
		exec.execute();

		Mockito.verify(context, Mockito.never()).setLoggedInTime(anyLong());
	}
}
