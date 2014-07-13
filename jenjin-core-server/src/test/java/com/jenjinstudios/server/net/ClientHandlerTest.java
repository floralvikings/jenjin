package com.jenjinstudios.server.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.server.sql.Authenticator;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Caleb Brinkman
 */
public class ClientHandlerTest
{
	@Test
	public void testSendFirstConnectResponse() throws Exception {
		MessageOutputStream messageOutputStream = mock(MessageOutputStream.class);
		MessageIO messageIO = mock(MessageIO.class);
		AuthServer server = mock(AuthServer.class);
		when(messageIO.getOut()).thenReturn(messageOutputStream);

		ClientHandler clientHandler = new ClientHandler(server, messageIO);
		clientHandler.sendFirstConnectResponse();
		clientHandler.writeAllMessages();

		verify(messageOutputStream, times(1)).writeMessage((Message) any());
	}

	@Test
	public void testSendLogoutStatus() throws Exception {
		MessageOutputStream messageOutputStream = mock(MessageOutputStream.class);
		MessageIO messageIO = mock(MessageIO.class);
		AuthServer server = mock(AuthServer.class);
		when(messageIO.getOut()).thenReturn(messageOutputStream);

		ClientHandler clientHandler = new ClientHandler(server, messageIO);
		clientHandler.sendLogoutStatus(true);
		clientHandler.writeAllMessages();

		verify(messageOutputStream, times(1)).writeMessage((Message) any());
	}

	@Test
	public void testSetID() throws Exception {
		MessageIO messageIO = mock(MessageIO.class);
		AuthServer server = mock(AuthServer.class);

		ClientHandler clientHandler = new ClientHandler(server, messageIO);
		clientHandler.setHandlerId(123);

		assertEquals(clientHandler.getHandlerId(), 123);
		assertEquals(clientHandler.getName(), "Client Handler 123");
	}

	@Test
	public void testShutDown() throws Exception {
		Authenticator authenticator = mock(Authenticator.class);
		User user = mock(User.class);
		MessageIO messageIO = mock(MessageIO.class);
		AuthServer server = mock(AuthServer.class);
		when(server.getAuthenticator()).thenReturn(authenticator);

		ClientHandler clientHandler = new ClientHandler(server, messageIO);
		clientHandler.setUser(user);
		clientHandler.shutdown();

		verify(authenticator).logOutUser(anyString());
		verify(server).removeClient(clientHandler);
	}

	@Test
	public void testLoggedInTime() {
		MessageIO messageIO = mock(MessageIO.class);
		AuthServer server = mock(AuthServer.class);
		ClientHandler clientHandler = new ClientHandler(server, messageIO);
		clientHandler.setLoggedInTime(12345l);
		assertEquals(clientHandler.getLoggedInTime(), 12345l);
	}
}
