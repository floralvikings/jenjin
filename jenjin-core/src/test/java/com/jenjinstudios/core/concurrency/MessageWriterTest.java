package com.jenjinstudios.core.concurrency;

import com.jenjinstudios.core.SimpleMessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageOutputStream;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test the MessageWriter class.
 *
 * @author Caleb Brinkman
 */
public class MessageWriterTest
{
	/**
	 * Test the asynchronous writing functionality of the MessageWriter class.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testAsynchronousWrite() throws Exception {
		Message message = mock(Message.class);
		MessageOutputStream outputStream = mock(MessageOutputStream.class);
		MessageContext messageContext = new SimpleMessageContext();

		MessageWriter writer = new MessageWriter(outputStream);
		writer.setMessageContext(messageContext);
		messageContext.enqueue(message);
		writer.start();
		// Give the writer time to write the message.
		Thread.sleep(100);
		verify(outputStream).writeMessage(message);
		writer.stop();
	}

	/**
	 * Test the isErrored function without an error being thrown.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testIsErrored() throws Exception {
		Message message = mock(Message.class);
		MessageOutputStream outputStream = mock(MessageOutputStream.class);
		MessageContext context = new SimpleMessageContext();

		MessageWriter writer = new MessageWriter(outputStream);
		context.enqueue(message);
		writer.start();
		// Give the writer time to write the message.
		Thread.sleep(100);
		Assert.assertFalse(writer.isErrored(), "Writer should not have errored.");
		writer.stop();
	}

	/**
	 * Test the isErrored function with an error being thrown.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testIsErroredWithError() throws Exception {
		Message message = mock(Message.class);
		MessageOutputStream outputStream = mock(MessageOutputStream.class);
		MessageContext context = new SimpleMessageContext();
		Mockito.doThrow(new IOException("Error")).when(outputStream).writeMessage(message);

		MessageWriter writer = new MessageWriter(outputStream);
		writer.setMessageContext(context);
		context.enqueue(message);
		writer.start();
		// Give the writer time to error.
		Thread.sleep(100);
		Assert.assertTrue(writer.isErrored(), "Writer should have errored.");
		writer.stop();
	}
}
