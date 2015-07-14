package com.jenjinstudios.core;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.connection.ConnectionConfig;
import com.jenjinstudios.core.io.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the {@code Connection} class.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("OverlyCoupledClass")
public class ConnectionTest
{
	private static final MessageRegistry MESSAGE_REGISTRY = MessageRegistry.getGlobalRegistry();
	private static final int INVALID_MESSAGE_ID = -255;
    private static final long REQUEST_TIME_SPOOF = 123456789L;

	/**
	 * Set up the message registry.
	 */
	@BeforeClass(groups = "unit")
	public void setUp() {
		MESSAGE_REGISTRY.register("Test Message Registry",
			  getClass().getClassLoader().getResourceAsStream("test/jenjinstudios/core/Messages.xml"));
		MESSAGE_REGISTRY.register("Core Message Registry",
			  getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/core/io/Messages.xml"));
	}

	/**
	 * Clear the message registry.
	 */
	@AfterClass(groups = "unit")
	public void clearMessageRegistry() {
		MESSAGE_REGISTRY.clear();
	}

	/**
	 * Test the {@code shutdown} method.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(groups = "unit")
	public void testShutDown() throws Exception {
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		InputStream in = dataInputStreamMock.getIn();
        dataInputStreamMock.mockReadShort((short) INVALID_MESSAGE_ID);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

		MessageInputStream messageInputStream = new MessageInputStream(in);
		MessageOutputStream messageOutputStream = new MessageOutputStream(bos);
		ConnectionConfig connectionConfig = mock(ConnectionConfig.class);
		when(connectionConfig.getContextClass()).thenReturn(MessageContext.class);
		Connection connection = new Connection<>(connectionConfig, messageInputStream, messageOutputStream);
		connection.shutdown();

		Assert.assertTrue(messageOutputStream.isClosed(), "MessageOutputStream should be closed");
	}

	/**
	 * Test the ping request functionality.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(groups = "unit")
	public void testPingRequest() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

        MessageInputStream messageInputStream = mock(MessageInputStream.class);
        MessageOutputStream messageOutputStream = new MessageOutputStream(bos);

        Message pingRequest = MESSAGE_REGISTRY.createMessage("PingRequest");
        pingRequest.setArgument("requestTimeMillis", REQUEST_TIME_SPOOF);

        when(messageInputStream.readMessage()).thenReturn(pingRequest).thenReturn(MESSAGE_REGISTRY.createMessage
              ("BlankMessage"));
		ConnectionConfig connectionConfig = mock(ConnectionConfig.class);
		when(connectionConfig.getContextClass()).thenReturn(MessageContext.class);
		Connection connection = new Connection<>(connectionConfig, messageInputStream, messageOutputStream);
		connection.start();
		Thread.sleep(100);
		connection.shutdown();

		// The connection should execute the InvalidExecutableMessage,
		byte[] bytes = bos.toByteArray();
		MessageInputStream mis = new MessageInputStream(new ByteArrayInputStream(bytes));
		Message msg = mis.readMessage();
        Assert.assertEquals(msg.name, "PingResponse", "Message not PingResponse");
    }

	/**
	 * Test the ping response functionality.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(groups = "unit")
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
		ConnectionConfig connectionConfig = mock(ConnectionConfig.class);
		when(connectionConfig.getContextClass()).thenReturn(MessageContext.class);
		Connection connection = new Connection<>(connectionConfig, messageInputStream, messageOutputStream);

		connection.start();
		Thread.sleep(100);
		connection.shutdown();

		// Ping time should be extremely close to 0, but taking into account wonkiness with tests, I'll allow
		// up to 1000
		Assert.assertEquals(connection.getMessageContext().getPingTracker().getAveragePingTime(), 0, 1000, "Ping " +
			  "response too high\n" +
			  "this may be a one off, try running again before digging too deeply.");
    }
}
