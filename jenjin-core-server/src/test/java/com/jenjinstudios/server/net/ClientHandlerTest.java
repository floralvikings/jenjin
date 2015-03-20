package com.jenjinstudios.server.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.BasicUser;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Caleb Brinkman
 */
@SuppressWarnings("unchecked")
public class ClientHandlerTest
{

	@Test
	public void testSendLogoutStatus() throws Exception {
		AuthServer server = mock(AuthServer.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageIO messageIO = new MessageIO(mis, mos);

		ClientHandler clientHandler = new ClientHandler(server, messageIO);
		clientHandler.sendLogoutStatus(true);
		try
		{
			clientHandler.getMessageIO().writeAllMessages();
		} catch (IOException e)
		{
			clientHandler.shutdown();
		}

		verify(mos, times(1)).writeMessage(any());
	}

	@Test
	public void testSetID() throws Exception {
		AuthServer server = mock(AuthServer.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageIO messageIO = new MessageIO(mis, mos);

		ClientHandler clientHandler = new ClientHandler(server, messageIO);
		clientHandler.setHandlerId(123);

		assertEquals(clientHandler.getHandlerId(), 123);
		assertEquals(clientHandler.getName(), "Client Handler 123");
	}

	@Test
	public void testShutDown() throws Exception {
		Authenticator<BasicUser> authenticator = mock(Authenticator.class);
		BasicUser user = mock(BasicUser.class);
		AuthServer server = mock(AuthServer.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageIO messageIO = new MessageIO(mis, mos);
		when(server.getAuthenticator()).thenReturn(authenticator);

		ClientHandler clientHandler = new ClientHandler(server, messageIO);
		clientHandler.setUser(user);
		clientHandler.shutdown();

		verify(authenticator).logOutUser(anyString());
		verify(server).removeClient(clientHandler);
	}

	@Test
	public void testLoggedInTime() {
		AuthServer server = mock(AuthServer.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageIO messageIO = new MessageIO(mis, mos);
		ClientHandler clientHandler = new ClientHandler(server, messageIO);
		clientHandler.setLoggedInTime(12345l);
		assertEquals(clientHandler.getLoggedInTime(), 12345l);
	}
}
