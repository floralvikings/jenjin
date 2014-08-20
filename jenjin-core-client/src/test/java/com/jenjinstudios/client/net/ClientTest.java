package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import org.testng.Assert;
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

	@Test
	public void testDoPostConnectInit() {
		MessageOutputStream out = mock(MessageOutputStream.class);
		MessageIO messageIO = mock(MessageIO.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		when(messageIO.getIn()).thenReturn(mis);
		when(messageIO.getOut()).thenReturn(out);
		when(out.isClosed()).thenReturn(false);
		Client client = new Client(messageIO);
		Message message = mock(Message.class);
		when(message.getArgument("ups")).thenReturn(50);
		client.doPostConnectInit(message);
		Assert.assertTrue(client.isInitialized());
		Assert.assertEquals(client.getUps(), 50);
	}
}
