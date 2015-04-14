package com.jenjinstudios.server.net;

import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.message.ServerMessageFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
@SuppressWarnings("unchecked")
@PrepareForTest(ServerMessageFactory.class)
public class ClientHandlerTest extends PowerMockTestCase
{

	@Test
	public void testShutDown() throws Exception {
		Authenticator<BasicUser> authenticator = mock(Authenticator.class);
		BasicUser user = mock(BasicUser.class);
		Server server = mock(Server.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);
		ServerMessageContext context = new ServerMessageContext();
		when(server.getAuthenticator()).thenReturn(authenticator);

		ClientHandler clientHandler = new ClientHandler(server, messageStreamPair, context);
		context.setUser(user);
		clientHandler.shutdown();

		verify(authenticator).logOutUser(anyString());
	}

}
