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
		TaskedServer<ClientHandler> taskedServer = new AuthServer<>(mr, 10, 51015, ClientHandler.class, a);
		Runnable r = Mockito.mock(Runnable.class);
		taskedServer.addRepeatedTask(r);
		taskedServer.start();
		taskedServer.shutdown();
		Mockito.verify(r).run();
	}
}
