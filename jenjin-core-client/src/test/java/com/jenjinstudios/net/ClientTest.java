package com.jenjinstudios.net;

import org.mockito.Mockito;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ClientTest
{
	@Test
	public void testAddRepeatedTask() {
		Runnable r = Mockito.mock(Runnable.class);

		Client client = new Client("127.0.0.1", 12345);
		client.addRepeatedTask(r);
		client.runRepeatedTasks();
		Mockito.verify(r).run();
	}

	@Test
	public void testDoPostConnectInit() {

	}
}
