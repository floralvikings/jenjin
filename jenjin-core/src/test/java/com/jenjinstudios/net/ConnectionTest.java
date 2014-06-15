package com.jenjinstudios.net;

import com.jenjinstudios.io.*;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	@Test(expectedExceptions = IOException.class)
	public void testCloseLink() throws Exception {
		MessageRegistry mr = new MessageRegistry();

		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		InputStream in = dataInputStreamMock.getIn();
		dataInputStreamMock.mockReadShort((short) -255);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		// Spoof the socket's input and output stream to ones that we control
		Socket sock = Mockito.mock(Socket.class);
		Mockito.when(sock.getInputStream()).thenReturn(in);
		Mockito.when(sock.getOutputStream()).thenReturn(bos);

		Connection connection = new Connection(mr);
		connection.setSocket(sock);
		connection.closeLink();

		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageName", "FooBar");
		msg.setArgument("messageID", (short) -255);
		connection.getOutputStream().writeMessage(msg);
	}

	@Test
	public void testSendPing() throws Exception {
		MessageRegistry mr = new MessageRegistry();
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadBoolean(true);
		InputStream in = dataInputStreamMock.getIn();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		// Spoof the socket's input and output stream to ones that we control
		Socket sock = Mockito.mock(Socket.class);
		Mockito.when(sock.getInputStream()).thenReturn(in);
		Mockito.when(sock.getOutputStream()).thenReturn(bos);

		Connection connection = new Connection(mr);
		connection.setSocket(sock);
		connection.sendPing();
		connection.sendAllMessages();
		connection.closeLink();

		byte[] bytes = bos.toByteArray();
		MessageInputStream mis = new MessageInputStream(mr, new ByteArrayInputStream(bytes));
		Message msg = mis.readMessage();
		Assert.assertEquals(msg.name, "PingRequest");
	}

	@Test
	public void testPingRequest() throws Exception {
		MessageRegistry mr = new MessageRegistry();
		// Spoof an invalid message
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) 1);
		dataInputStreamMock.mockReadLong(123456789);
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
		Assert.assertEquals(msg.name, "PingResponse");
	}
}
