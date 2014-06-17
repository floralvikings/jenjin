package com.jenjinstudios.net;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageOutputStream;
import com.jenjinstudios.io.MessageRegistry;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
	public void testDoPostConnectInit() throws Exception {
		int ups = 100;
		int period = 1000 / ups;
		// Build a FirstConnectResponse message
		MessageRegistry mr = new MessageRegistry();
		Message fcr = mr.createMessage("FirstConnectResponse");
		fcr.setArgument("ups", ups);

		// Mock a stream containing a FirstConnectResponse
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(mr, bos);
		mos.writeMessage(fcr);
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		// Mock a socket which returns the mocked stream.
		Socket sock = Mockito.mock(Socket.class);
		Mockito.when(sock.getInputStream()).thenReturn(bis);
		// Doesn't really matter, just has to have a valid stream
		Mockito.when(sock.getOutputStream()).thenReturn(bos);

		Client client = new Client(sock);
		client.run();

		Assert.assertEquals(client.getPeriod(), period);
	}
}
