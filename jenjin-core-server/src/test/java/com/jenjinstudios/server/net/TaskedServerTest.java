package com.jenjinstudios.server.net;

import com.jenjinstudios.server.sql.Authenticator;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class TaskedServerTest
{
	@Test
	@SuppressWarnings("unchecked")
	public void testGetAverageUPS() throws Exception {
		ServerInit serverInit = Mockito.mock(ServerInit.class);
		Authenticator authenticator = Mockito.mock(Authenticator.class);
		Mockito.when(serverInit.getHandlerClass()).thenReturn(ClientHandler.class);
		Mockito.when(serverInit.getUps()).thenReturn(50);
		Mockito.when(serverInit.getPort()).thenReturn(51015);
		AuthServer<ClientHandler> taskedServer = new AuthServer<ClientHandler>(serverInit, authenticator);
		taskedServer.start();
		Thread.sleep(5000);
		Assert.assertEquals(taskedServer.getAverageUPS(), taskedServer.getUps(), 1.0);
	}
}
