package com.jenjinstudios.core;

import com.jenjinstudios.core.io.*;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author Caleb Brinkman
 */
public class ConnectionTest
{
	private static MessageRegistry mr = new MessageRegistry();

	@Test
	public void testProcessMessage() throws Exception {
		// Spoof an invalid message
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) -255);
		dataInputStreamMock.mockReadShort((short) -1);
		dataInputStreamMock.mockReadBoolean(false);
		dataInputStreamMock.mockReadUtf("FooBar");
		dataInputStreamMock.mockReadShort((short) -1);
		InputStream in = dataInputStreamMock.getIn();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		MessageInputStream messageInputStream = new MessageInputStream(mr, in);
		MessageOutputStream messageOutputStream = new MessageOutputStream(mr, bos);
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream, mr);
		Connection connection = new Connection(messageIO);
		connection.start();
		Thread.sleep(100);
		connection.runQueuedExecutableMessages();
		connection.writeAllMessages();
		connection.shutdown();

		// The connection should execute the InvalidExecutableMessage,
		byte[] bytes = bos.toByteArray();
		MessageInputStream mis = new MessageInputStream(mr, new ByteArrayInputStream(bytes));
		Message msg = mis.readMessage();
		Assert.assertEquals(msg.getArgument("messageName"), "Unknown");
	}

	@Test(expectedExceptions = MessageQueueException.class)
	public void testCloseLink() throws Exception {
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		InputStream in = dataInputStreamMock.getIn();
		dataInputStreamMock.mockReadShort((short) -255);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		MessageInputStream messageInputStream = new MessageInputStream(mr, in);
		MessageOutputStream messageOutputStream = new MessageOutputStream(mr, bos);
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream, mr);
		Connection connection = new Connection(messageIO);
		connection.closeLink();

		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageName", "FooBar");
		msg.setArgument("messageID", (short) -255);
		connection.queueOutgoingMessage(msg);
		connection.writeAllMessages();
	}

	@Test
	public void testPingRequest() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		MessageInputStream messageInputStream = Mockito.mock(MessageInputStream.class);
		MessageOutputStream messageOutputStream = new MessageOutputStream(mr, bos);

		Message pingRequest = mr.createMessage("PingRequest");
		pingRequest.setArgument("requestTimeNanos", 123456789l);

		Mockito.when(messageInputStream.readMessage()).thenReturn(pingRequest).thenReturn(mr.createMessage
			  ("BlankMessage"));
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream, mr);
		Connection connection = new Connection(messageIO);
		connection.start();
		Thread.sleep(100);
		connection.runQueuedExecutableMessages();
		connection.writeAllMessages();
		connection.shutdown();

		// The connection should execute the InvalidExecutableMessage,
		byte[] bytes = bos.toByteArray();
		MessageInputStream mis = new MessageInputStream(mr, new ByteArrayInputStream(bytes));
		Message msg = mis.readMessage();
		Assert.assertEquals(msg.name, "PingResponse");
	}

	@Test
	public void testPingResponse() throws Exception {
		// Spoof an invalid message
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) 2);
		dataInputStreamMock.mockReadLong(System.nanoTime());
		InputStream in = dataInputStreamMock.getIn();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		MessageInputStream messageInputStream = new MessageInputStream(mr, in);
		MessageOutputStream messageOutputStream = new MessageOutputStream(mr, bos);
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream, mr);
		Connection connection = new Connection(messageIO);

		// Create and run the connection.  Normally, we would use connection.start() to spawn a new thread
		// but for testing purposes we want the connection to run in the current thread.
		connection.start();
		Thread.sleep(100);
		// Again, normally an implementation would schedule this, but that's excessive for testing purposes
		connection.runQueuedExecutableMessages();
		connection.writeAllMessages();
		connection.shutdown();

		// Ping time should be extremely close to 0, but taking into account wonkiness with tests, I'll allow
		// up to 1000
		Assert.assertEquals(connection.getPingTracker().getAveragePingTime(), 0, 1000);
	}
}
