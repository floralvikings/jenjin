package com.jenjinstudios.net;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.net.Socket;

/**
 * @author Caleb Brinkman
 */
public class ClientTest
{
	@Test
	public void testAddRepeatedTask() {
		Runnable r = Mockito.mock(Runnable.class);
		Socket sock = Mockito.mock(Socket.class);
		Client client = new Client(sock);
		client.addRepeatedTask(r);
		client.runRepeatedTasks();
		Mockito.verify(r).run();
	}

	@Test
	public void testDoPostConnectInit() {

	}
}
