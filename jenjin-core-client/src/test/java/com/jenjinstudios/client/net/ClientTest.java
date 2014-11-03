package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
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
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageIO messageIO = new MessageIO(mis, mos);
		Runnable r = mock(Runnable.class);
		Client client = new Client(messageIO);
		client.addRepeatedTask(r);
		client.runRepeatedTasks();
		verify(r).run();
	}

}
