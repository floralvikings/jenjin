package com.jenjinstudios.server.message;

import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.Server;
import com.jenjinstudios.server.net.ServerMessageContext;
import com.jenjinstudios.server.net.ServerUpdateTask;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
@PrepareForTest(ServerMessageFactory.class)
public class ExecutableLogoutRequestTest extends PowerMockTestCase
{
	@SuppressWarnings("unchecked")
	@Test
	public void testMessageExecution() throws Exception {
		PowerMockito.mockStatic(ServerMessageFactory.class);
		when(ServerMessageFactory.generateLogoutResponse(anyBoolean())).thenReturn(mock(Message.class));

		MessageRegistry messageRegistry = MessageRegistry.getGlobalRegistry();
		Message logoutRequest = messageRegistry.createMessage("LogoutRequest");

		User user = new BasicUser();
		user.setUsername("foo");
		user.setUsername("bar");
		ClientHandler clientHandler = mock(ClientHandler.class);
		Server server = mock(Server.class);
		Authenticator<BasicUser> authenticator = mock(Authenticator.class);
		ServerMessageContext context = mock(ServerMessageContext.class);
		when(server.getAuthenticator()).thenReturn(authenticator);
		ServerUpdateTask serverUpdateTask = mock(ServerUpdateTask.class);
		when(server.getServerUpdateTask()).thenReturn(serverUpdateTask);
		when(serverUpdateTask.getCycleStartTime()).thenReturn(12345L);
		when(authenticator.logOutUser(Mockito.<String>any())).thenReturn(new BasicUser());
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getUser()).thenReturn(user);

		ExecutableMessage exec = new ExecutableLogoutRequest(clientHandler, logoutRequest, context);
		exec.execute();

		verify(context).setUser(null);
	}
}
