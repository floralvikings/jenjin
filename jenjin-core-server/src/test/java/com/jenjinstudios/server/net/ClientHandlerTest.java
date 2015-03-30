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
import static org.testng.Assert.assertEquals;

/**
 * @author Caleb Brinkman
 */
@SuppressWarnings("unchecked")
@PrepareForTest(ServerMessageFactory.class)
public class ClientHandlerTest extends PowerMockTestCase
{

	@Test
	public void testSetID() throws Exception {
		Server server = mock(Server.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);

		ClientHandler clientHandler = new ClientHandler(server, messageStreamPair);
		clientHandler.setHandlerId(123);

		assertEquals(clientHandler.getHandlerId(), 123);
		assertEquals(clientHandler.getName(), "Client Handler 123");
	}

	@Test
	public void testShutDown() throws Exception {
		Authenticator<BasicUser> authenticator = mock(Authenticator.class);
		BasicUser user = mock(BasicUser.class);
		Server server = mock(Server.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);
		when(server.getAuthenticator()).thenReturn(authenticator);

		ClientHandler clientHandler = new ClientHandler(server, messageStreamPair);
		clientHandler.setUser(user);
		clientHandler.shutdown();

		verify(authenticator).logOutUser(anyString());
		verify(server).removeClient(clientHandler);
	}

	@Test
	public void testLoggedInTime() {
		Server server = mock(Server.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);
		ClientHandler clientHandler = new ClientHandler(server, messageStreamPair);
		clientHandler.setLoggedInTime(12345l);
		assertEquals(clientHandler.getLoggedInTime(), 12345l);
	}
}
