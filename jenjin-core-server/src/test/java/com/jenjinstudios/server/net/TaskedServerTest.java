package com.jenjinstudios.server.net;

import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.sql.Authenticator;
import com.jenjinstudios.server.sql.AuthenticatorTest;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.sql.Connection;

/**
 * @author Caleb Brinkman
 */
public class TaskedServerTest
{
	@Test
	public void testAddRepeatedTask() throws Exception {
		MessageRegistry mr = new MessageRegistry();
		Connection testConnection = AuthenticatorTest.createTestConnection();
		Authenticator a = new Authenticator(testConnection);
		ClientListenerInit<ClientHandler> listenerInit = new ClientListenerInit<>(ClientHandler.class, 51021);
		ServerInit<ClientHandler> serverInit = new ServerInit<>(mr, 50, listenerInit);
		TaskedServer<ClientHandler> taskedServer = new AuthServer<>(serverInit, a);
		Runnable r = Mockito.mock(Runnable.class);
		taskedServer.addRepeatedTask(r);
		taskedServer.start();
		taskedServer.shutdown();
		Mockito.verify(r).run();
	}
}
