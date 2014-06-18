package com.jenjinstudios.net;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageOutputStream;
import com.jenjinstudios.io.MessageRegistry;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	@Test
	public void testRunFailure() throws Exception {
		int ups = 100;
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
		// Stream throws an IOException, causing run to exit before attempting to process messages.
		Mockito.when(sock.getOutputStream()).thenThrow(new IOException());

		Client client = new Client(sock);
		client.run();

		// We can verify that run() aborted early by checking to see if the period has been set
		// Since there is already a FirstConnectResponse waiting in the InputStream, if run does
		// not abort early, the period will be equal to 1000 / ups, as above.
		Assert.assertEquals(client.getPeriod(), 0);
	}
}
