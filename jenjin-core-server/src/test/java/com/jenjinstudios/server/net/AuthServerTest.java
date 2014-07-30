package com.jenjinstudios.server.net;

import com.jenjinstudios.server.sql.Authenticator;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
@SuppressWarnings("unchecked")
public class AuthServerTest
{
	@Test
	public void testConstructor() throws Exception {
		ServerInit<ClientHandler> serverInit = mock(ServerInit.class);
		Authenticator authenticator = mock(Authenticator.class);
		when(serverInit.getUps()).thenReturn(1);
		when(serverInit.getPort()).thenReturn(12345);
		when(serverInit.getHandlerClass()).thenReturn(ClientHandler.class);

		AuthServer<ClientHandler> authServer = new AuthServer<>(serverInit, authenticator);
		authServer.shutdown();

		Assert.assertEquals(authServer.getAuthenticator(), authenticator);
	}
}
