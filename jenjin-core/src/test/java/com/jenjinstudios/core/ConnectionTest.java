package com.jenjinstudios.core;

import com.jenjinstudios.core.io.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

/**
 * Test the {@code Connection} class.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("OverlyCoupledClass")
public class ConnectionTest
{
    private static final MessageRegistry MESSAGE_REGISTRY = MessageRegistry.getInstance();
    private static final int INVALID_MESSAGE_ID = -255;
    private static final long REQUEST_TIME_SPOOF = 123456789l;

    /**
     * Test the {@code processMessage} method.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testProcessMessage() throws Exception {
		// Spoof an invalid message
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
        dataInputStreamMock.mockReadShort((short) INVALID_MESSAGE_ID);
        dataInputStreamMock.mockReadShort((short) -1);
		dataInputStreamMock.mockReadBoolean(false);
		dataInputStreamMock.mockReadUtf("FooBar");
		dataInputStreamMock.mockReadShort((short) -1);
		InputStream in = dataInputStreamMock.getIn();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		MessageInputStream messageInputStream = new MessageInputStream(in);
		MessageOutputStream messageOutputStream = new MessageOutputStream(bos);
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream);
		Connection connection = new Connection(messageIO);
		connection.start();
		Thread.sleep(100);
		connection.getExecutableMessageQueue().runQueuedExecutableMessages();
		try
		{
			connection.getMessageIO().writeAllMessages();
		} catch (IOException e)
		{
			connection.shutdown();
		}
		connection.shutdown();

		// The connection should execute the InvalidExecutableMessage,
		byte[] bytes = bos.toByteArray();
		MessageInputStream mis = new MessageInputStream(new ByteArrayInputStream(bytes));
		Message msg = mis.readMessage();
		Assert.assertEquals(msg.getArgument("messageName"), "Unknown");
	}

	/**
	 * Test the {@code shutdown} method.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(expectedExceptions = MessageQueueException.class)
	public void testShutDown() throws Exception {
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		InputStream in = dataInputStreamMock.getIn();
        dataInputStreamMock.mockReadShort((short) INVALID_MESSAGE_ID);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

		MessageInputStream messageInputStream = new MessageInputStream(in);
		MessageOutputStream messageOutputStream = new MessageOutputStream(bos);
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream);
		Connection connection = new Connection(messageIO);
		connection.shutdown();

        Message msg = MESSAGE_REGISTRY.createMessage("InvalidMessage");
        msg.setArgument("messageName", "FooBar");
        msg.setArgument("messageID", (short) INVALID_MESSAGE_ID);
        connection.getMessageIO().queueOutgoingMessage(msg);
		try
		{
			connection.getMessageIO().writeAllMessages();
		} catch (IOException e)
		{
			connection.shutdown();
		}
	}

	/**
	 * Test the ping request functionality.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testPingRequest() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

        MessageInputStream messageInputStream = mock(MessageInputStream.class);
        MessageOutputStream messageOutputStream = new MessageOutputStream(bos);

        Message pingRequest = MESSAGE_REGISTRY.createMessage("PingRequest");
        pingRequest.setArgument("requestTimeMillis", REQUEST_TIME_SPOOF);

        when(messageInputStream.readMessage()).thenReturn(pingRequest).thenReturn(MESSAGE_REGISTRY.createMessage
              ("BlankMessage"));
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream);
		Connection connection = new Connection(messageIO);
		connection.start();
		Thread.sleep(100);
		connection.getExecutableMessageQueue().runQueuedExecutableMessages();
		try
		{
			connection.getMessageIO().writeAllMessages();
		} catch (IOException e)
		{
			connection.shutdown();
		}
		connection.shutdown();

		// The connection should execute the InvalidExecutableMessage,
		byte[] bytes = bos.toByteArray();
		MessageInputStream mis = new MessageInputStream(new ByteArrayInputStream(bytes));
		Message msg = mis.readMessage();
		Assert.assertEquals(msg.name, "PingResponse");
	}

	/**
	 * Test the ping response functionality.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testPingResponse() throws Exception {
		// Spoof an invalid message
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) 3);
		dataInputStreamMock.mockReadInt(1);
		dataInputStreamMock.mockReadByte((byte) 0);
		dataInputStreamMock.mockReadShort((short) 2);
		dataInputStreamMock.mockReadLong(System.currentTimeMillis());
		InputStream in = dataInputStreamMock.getIn();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		MessageInputStream messageInputStream = new MessageInputStream(in);
		MessageOutputStream messageOutputStream = new MessageOutputStream(bos);
		MessageIO messageIO = new MessageIO(messageInputStream, messageOutputStream);
		Connection connection = new Connection(messageIO);

		// Create and run the connection.  Normally, we would use connection.start() to spawn a new thread
		// but for testing purposes we want the connection to run in the current thread.
		connection.start();
		Thread.sleep(100);
		// Again, normally an implementation would schedule this, but that's excessive for testing purposes
		connection.getExecutableMessageQueue().runQueuedExecutableMessages();
		try
		{
			connection.getMessageIO().writeAllMessages();
		} catch (IOException e)
		{
			connection.shutdown();
		}
		connection.shutdown();

		// Ping time should be extremely close to 0, but taking into account wonkiness with tests, I'll allow
		// up to 1000
		Assert.assertEquals(connection.getPingTracker().getAveragePingTime(), 0, 1000);
	}
}
