package com.jenjinstudios.net;

import com.jenjinstudios.io.DataInputStreamMock;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageInputStream;
import com.jenjinstudios.io.MessageRegistry;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author Caleb Brinkman
 */
public class ConnectionTest
{
	@Test
	public void testProcessMessage() throws Exception {
		MessageRegistry mr = new MessageRegistry();
		// Spoof an invalid message
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) -255);
		dataInputStreamMock.mockReadShort((short) -1);
		dataInputStreamMock.mockReadBoolean(false);
		dataInputStreamMock.mockReadUtf("FooBar");
		dataInputStreamMock.mockReadShort((short) -1);
		InputStream in = dataInputStreamMock.getIn();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		// Spoof the socket's input and output stream to ones that we control
		Socket sock = Mockito.mock(Socket.class);
		Mockito.when(sock.getInputStream()).thenReturn(in);
		Mockito.when(sock.getOutputStream()).thenReturn(bos);

		// Create and run the connection.  Normally, we would use connection.start() to spawn a new thread
		// but for testing purposes we want the connection to run in the current thread.
		Connection connection = new Connection(mr);
		connection.setSocket(sock);
		connection.run();
		// Again, normally an implementation would schedule this, but that's excessive for testing purposes
		connection.runSyncedTasks();
		connection.sendAllMessages();

		// The connection should execute the InvalidExecutableMessage,
		byte[] bytes = bos.toByteArray();
		MessageInputStream mis = new MessageInputStream(mr, new ByteArrayInputStream(bytes));
		Message msg = mis.readMessage();
		Assert.assertEquals(msg.getArgument("messageName"), "Unknown");
	}
}
