package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ClientTest
{
	@Test
	public void testAddRepeatedTask() {
		MessageIO messageIO = mock(MessageIO.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		when(messageIO.getIn()).thenReturn(mis);
		when(messageIO.getOut()).thenReturn(mos);
		Runnable r = mock(Runnable.class);
		Client client = new Client(messageIO);
		client.addRepeatedTask(r);
		client.runRepeatedTasks();
		verify(r).run();
	}

}
