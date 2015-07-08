package com.jenjinstudios.core.concurrency;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Deque;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the MessageReader class.
 *
 * @author Caleb Brinkman
 */
public class MessageReaderTest
{
	/**
	 * Test the asynchronous reading and storage of incoming messages.
	 *
	 * @throws Exception If there's and exception.
	 */
	@Test(groups = "unit")
	public void testAsynchronousReading() throws Exception {
		Message message = mock(Message.class);
		when(message.getArgument("requestTimeMillis")).thenReturn(12345L);
		MessageInputStream inputStream = mock(MessageInputStream.class);
		// Mock a blocking read.
		when(inputStream.readMessage()).thenReturn(message).then(invocationOnMock -> {
			Thread.sleep(10000);
			return null;
		});
		MessageReader reader = new MessageReader(inputStream);
		reader.start();

		// Wait a little bit to make sure the timer can run at least once.
		Thread.sleep(100);

		Deque<Message> receivedMessages = reader.getReceivedMessages();
		Assert.assertEquals(receivedMessages.size(), 1, "Should have one received message");

		reader.stop();
	}

	/**
	 * Test the isErrored method.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(groups = "unit")
	public void testIsErrored() throws Exception {
		MessageInputStream inputStream = mock(MessageInputStream.class);
		// Mock a blocking read.
		when(inputStream.readMessage()).then(invocationOnMock -> {
			Thread.sleep(10000);
			return null;
		});
		MessageReader reader = new MessageReader(inputStream);
		reader.start();
		Assert.assertFalse(reader.isErrored(), "Reader should not error.");
		reader.stop();
	}

	/**
	 * Test the isErrored method with an error caused.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(groups = "unit")
	public void testIsErroredWithError() throws Exception {
		MessageInputStream inputStream = mock(MessageInputStream.class);
		// Mock a blocking read.
		when(inputStream.readMessage()).thenThrow(new IOException("Error")).then(invocationOnMock -> {
			Thread.sleep(10000);
			return null;
		});
		MessageReader reader = new MessageReader(inputStream);
		reader.start();
		// Give the reader time to encounter the error.
		Thread.sleep(100);
		Assert.assertTrue(reader.isErrored(), "Reader should have error.");
		reader.stop();
	}
}
