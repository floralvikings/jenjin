package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Caleb Brinkman
 */
public class ClientTest
{
	@Test
	public void testAddRepeatedTask() {
		MessageIO messageIO = mock(MessageIO.class);
		Runnable r = mock(Runnable.class);
		Client client = new Client(messageIO);
		client.addRepeatedTask(r);
		client.runRepeatedTasks();
		verify(r).run();
	}

	@Test
	public void testDoPostConnectInit() {
		// TODO Write this test
	}
}
